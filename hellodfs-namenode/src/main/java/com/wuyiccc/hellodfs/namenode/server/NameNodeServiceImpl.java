package com.wuyiccc.hellodfs.namenode.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuyiccc.hellodfs.namenode.rpc.model.*;
import com.wuyiccc.hellodfs.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.stub.StreamObserver;

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
     * already sync max TxId
     */
    private volatile long backupSyncTxId = 0L;

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

        RegisterResponse response = RegisterResponse.newBuilder()
                .setStatus(STATUS_SUCCESS)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void heartBeat(HeartBeatRequest request, StreamObserver<HeartBeatResponse> responseObserver) {
        dataNodeManager.heartBeat(request.getIp(), request.getHostname());

        HeartBeatResponse response = HeartBeatResponse.newBuilder()
                .setStatus(STATUS_SUCCESS)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void mkdir(MkdirRequest request, StreamObserver<MkdirResponse> responseObserver) {
        try {

            MkdirResponse response = null;
            if (!isRunning) {
                response = MkdirResponse.newBuilder()
                        .setStatus(STATUS_SHUTDOWN)
                        .build();
            } else {
                this.fsNameSystem.mkdir(request.getPath());
                response = MkdirResponse.newBuilder()
                        .setStatus(STATUS_SUCCESS)
                        .build();
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

    @Override
    public void fetchEditsLog(FetchEditsLogRequest request, StreamObserver<FetchEditsLogResponse> responseObserver) {

        FetchEditsLogResponse response = null;
        JSONArray fetchedEditsLog = new JSONArray();

        // get already flushed into disk max txId
        List<String> flushedTxIds = this.fsNameSystem.getFsEditLog().getFlushedTxIds();

        // data all in memory cache
        if (flushedTxIds.size() == 0) {

            if (this.backupSyncTxId != 0) {
                this.currentBufferedEditsLog.clear();

                String[] bufferedEditsLog = this.fsNameSystem.getFsEditLog().getBufferedEditsLog();
                for (String editLog : bufferedEditsLog) {
                    currentBufferedEditsLog.add(JSONObject.parseObject(editLog));
                }

                int fetchCount = 0;

                for (int i = 0; i < this.currentBufferedEditsLog.size(); i++) {
                    if (this.currentBufferedEditsLog.getJSONObject(i).getLong("txId") > this.backupSyncTxId) {
                        fetchedEditsLog.add(this.currentBufferedEditsLog.getJSONObject(i));
                        this.backupSyncTxId = this.currentBufferedEditsLog.getJSONObject(i).getLong("txId");
                        fetchCount++;
                    }

                    if (fetchCount == BACKUP_NODE_FETCH_SIZE) {
                        break;
                    }
                }
            } else {
                String[] bufferedEditsLog = this.fsNameSystem.getFsEditLog().getBufferedEditsLog();
                for (String editLog : bufferedEditsLog) {
                    this.currentBufferedEditsLog.add(JSONObject.parseObject(editLog));
                }


                int fetchSize = Math.min(BACKUP_NODE_FETCH_SIZE, this.currentBufferedEditsLog.size());

                for (int i = 0; i < fetchSize; i++) {
                    fetchedEditsLog.add(this.currentBufferedEditsLog.getJSONObject(i));
                    if (i == fetchSize - 1) {
                        this.backupSyncTxId = this.currentBufferedEditsLog.getJSONObject(i).getLong("txId");
                    }
                }
            }

            response = FetchEditsLogResponse.newBuilder().setEditsLog(fetchedEditsLog.toJSONString()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();


        }
    }
}
