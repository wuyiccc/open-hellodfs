package com.wuyiccc.hellodfs.namenode.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuyiccc.hellodfs.namenode.rpc.model.*;
import com.wuyiccc.hellodfs.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author wuyiccc
 * @date 2022/5/2 14:22
 */
public class NameNodeServiceImpl implements NameNodeServiceGrpc.NameNodeService {

    public static final Integer STATUS_SUCCESS = 1;

    public static final Integer STATUS_FAILURE = 2;

    public static final Integer STATUS_SHUTDOWN = 3;

    public static final Integer BACKUP_NODE_FETCH_SIZE = 10;


    private FSNameSystem fsNameSystem;

    private DataNodeManager dataNodeManager;

    private volatile Boolean isRunning = true;

    /**
     * backupNode already sync max TxId
     */
    private long syncedTxId = 0L;

    /**
     * current memory cache already flushed into disk txId startTxId-endTxId scope
     */
    private String bufferedFlushedTxId;

    /**
     * cached editsLog from current buffer or editsLog disk file
     */
    private JSONArray currentBufferedEditsLog = new JSONArray();

    public NameNodeServiceImpl(FSNameSystem fsNameSystem, DataNodeManager dataNodeManager) {
        this.fsNameSystem = fsNameSystem;
        this.dataNodeManager = dataNodeManager;
    }


    /**
     * take over datanode register
     */
    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        dataNodeManager.register(request.getIp(), request.getHostname());

        RegisterResponse response = RegisterResponse.newBuilder().setStatus(STATUS_SUCCESS).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void heartBeat(HeartBeatRequest request, StreamObserver<HeartBeatResponse> responseObserver) {
        dataNodeManager.heartBeat(request.getIp(), request.getHostname());

        HeartBeatResponse response = HeartBeatResponse.newBuilder().setStatus(STATUS_SUCCESS).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void mkdir(MkdirRequest request, StreamObserver<MkdirResponse> responseObserver) {
        try {

            MkdirResponse response = null;
            if (!isRunning) {
                response = MkdirResponse.newBuilder().setStatus(STATUS_SHUTDOWN).build();
            } else {
                this.fsNameSystem.mkdir(request.getPath());
                response = MkdirResponse.newBuilder().setStatus(STATUS_SUCCESS).build();
            }
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown(ShutdownRequest request, StreamObserver<ShutdownResponse> responseObserver) {
        this.isRunning = false;
        this.fsNameSystem.flush();
    }

    /**
     * fetch return size <= 10
     */
    @Override
    public void fetchEditsLog(FetchEditsLogRequest request, StreamObserver<FetchEditsLogResponse> responseObserver) {

        FetchEditsLogResponse response = null;
        JSONArray fetchedEditsLog = new JSONArray();

        // get already flushed into disk max txId
        List<String> flushedTxIds = this.fsNameSystem.getFsEditLog().getFlushedTxIds();

        // data all in memory cache
        if (flushedTxIds.size() == 0) {
            this.fetchFromBufferedEditsLog(fetchedEditsLog);

            // if already has editLog flush into disk:
            // you want fetch txId scope has three condition -> two conditions
            // 1. currentBufferedEditsLog has already cached flushed into disk editsLog file
            // 1.1. all in disk: only get first disk file
            // 1.2. disk + current buffer memory: only get disk file
            // 1.3. all in memory
            // 2. currentBufferedEditsLog hasn't cached flushed into disk editsLog file, means capacity is empty
            // 2.1. all in disk: only get first disk file
            // 2.2. disk + current buffer memory: only get disk file
            // 2.3. all in memory
        } else {
            // if currentBufferedEditsLog already cached some editsLog flushed into disk file
            if (this.bufferedFlushedTxId != null) {
                // if you want fetched editsLog already flushed into disk file, and cached into currentBufferedEditsLog -> 1.1 + 1.2
                if (existInFlushedFile(bufferedFlushedTxId)) {
                    fetchFromCurrentBuffer(fetchedEditsLog);
                } else {
                    String nextFlushedTxId = getNextFlushedTxId(flushedTxIds, bufferedFlushedTxId);
                    // if you want fetched editsLog already flushed into disk file, and not cached into currentBufferedEditsLog -> 1.1 + 1.2
                    if (nextFlushedTxId != null) {
                        fetchFromFlushedFile(nextFlushedTxId, fetchedEditsLog);
                        // if you want fetched editsLog is in doubleBuffer's currentBuffer -> 1.3
                    } else {
                        fetchFromBufferedEditsLog(fetchedEditsLog);
                    }
                }

                // if currentBufferedEditsLog hasn't cached any editsLog flushed into disk file
            } else {
                boolean fetchedFromFlushedFile = false;

                for (String flushedTxId : flushedTxIds) {
                    // if you want fetched editsLog file already flushed into disk -> 2.1 + 2.2
                    if (existInFlushedFile(flushedTxId)) {
                        fetchFromFlushedFile(flushedTxId, fetchedEditsLog);
                        fetchedFromFlushedFile = true;
                        break;
                    }
                }

                // 2.3
                if (!fetchedFromFlushedFile) {
                    fetchFromBufferedEditsLog(fetchedEditsLog);
                }
            }
        }

        response = FetchEditsLogResponse.newBuilder().setEditsLog(fetchedEditsLog.toJSONString()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    /**
     * @param flushedTxId startTxId_endTxId
     */
    private Boolean existInFlushedFile(String flushedTxId) {
        String[] flushedTxIdSplit = flushedTxId.split("_");
        long startTxId = Long.parseLong(flushedTxIdSplit[0]);
        long endTxId = Long.parseLong(flushedTxIdSplit[1]);
        long fetchTxId = this.syncedTxId + 1;

        if (fetchTxId >= startTxId && fetchTxId <= endTxId) {
            return true;
        }
        return false;
    }

    private String getNextFlushedTxId(List<String> flushedTxIds, String bufferedFlushedTxId) {
        for (int i = 0; i < flushedTxIds.size(); i++) {
            if (flushedTxIds.get(i).equals(bufferedFlushedTxId)) {
                if (i + 1 < flushedTxIds.size()) {
                    return flushedTxIds.get(i + 1);
                }
            }
        }
        return null;
    }

    /**
     * transfer editsLog file data into currentBufferedEditsLog
     */
    private void fetchFromFlushedFile(String flushedTxId, JSONArray fetchedEditsLog) {
        try {
            String[] flushedTxIdSplit = flushedTxId.split("_");
            long startTxId = Long.parseLong(flushedTxIdSplit[0]);
            long endTxId = Long.parseLong(flushedTxIdSplit[1]);

            String currentEditsLogFile = "E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\editslog\\edits-" + (startTxId) + "-" + endTxId + ".log";
            List<String> editsLog = Files.readAllLines(Paths.get(currentEditsLogFile), StandardCharsets.UTF_8);

            this.currentBufferedEditsLog.clear();
            for (String editLog : editsLog) {
                this.currentBufferedEditsLog.add(JSONObject.parseObject(editLog));
            }

            // cached flushed into disk file startTxId_endTxtId which transfer to currentBufferedEditsLog
            this.bufferedFlushedTxId = flushedTxId;

            fetchFromCurrentBuffer(fetchedEditsLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * transfer doubleBuffer's current buffer into currentBufferedEditsLog
     */
    private void fetchFromBufferedEditsLog(JSONArray fetchedEditsLog) {
        this.currentBufferedEditsLog.clear();
        String[] bufferedEditsLog = this.fsNameSystem.getFsEditLog().getBufferedEditsLog();
        for (String editLog : bufferedEditsLog) {
            this.currentBufferedEditsLog.add(JSONObject.parseObject(editLog));
        }
        this.bufferedFlushedTxId = null;
        fetchFromCurrentBuffer(fetchedEditsLog);
    }


    /**
     * fetch editsLog data from currentBufferedEditsLog
     */
    private void fetchFromCurrentBuffer(JSONArray fetchedEditsLog) {
        int fetchCount = 0;
        for (int i = 0; i < this.currentBufferedEditsLog.size(); i++) {
            if (this.currentBufferedEditsLog.getJSONObject(i).getLong("txId") == this.syncedTxId + 1) {
                fetchedEditsLog.add(this.currentBufferedEditsLog.getJSONObject(i));
                this.syncedTxId = this.currentBufferedEditsLog.getJSONObject(i).getLong("txId");
                fetchCount++;
            }
            if (fetchCount == BACKUP_NODE_FETCH_SIZE) {
                break;
            }
        }
    }


}
