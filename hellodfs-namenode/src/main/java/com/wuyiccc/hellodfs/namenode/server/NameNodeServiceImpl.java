package com.wuyiccc.hellodfs.namenode.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuyiccc.hellodfs.namenode.rpc.model.*;
import com.wuyiccc.hellodfs.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.stub.StreamObserver;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wuyiccc
 * @date 2022/5/2 14:22
 */
public class NameNodeServiceImpl implements NameNodeServiceGrpc.NameNodeService {

    public static final Integer STATUS_SUCCESS = 1;

    public static final Integer STATUS_FAILURE = 2;

    public static final Integer STATUS_SHUTDOWN = 3;

    public static final Integer STATUS_DUPLICATE = 4;

    public static final Integer BACKUP_NODE_FETCH_SIZE = 10;


    private FSNameSystem fsNameSystem;

    private DataNodeManager dataNodeManager;

    private volatile Boolean isRunning = true;


    /**
     * current memory cache already flushed into disk txId startTxId-endTxId scope
     */
    private String bufferedFlushedTxId;

    /**
     * cached editsLog from current buffer or editsLog disk file
     */
    private JSONArray currentBufferedEditsLog = new JSONArray();

    private long currentBufferedTxId = 0L;

    public NameNodeServiceImpl(FSNameSystem fsNameSystem, DataNodeManager dataNodeManager) {
        this.fsNameSystem = fsNameSystem;
        this.dataNodeManager = dataNodeManager;
    }


    /**
     * take over datanode register
     */
    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        Boolean res = dataNodeManager.register(request.getIp(), request.getHostname(), request.getNioPort());

        RegisterResponse response = null;

        if (res) {
            response = RegisterResponse.newBuilder().setStatus(STATUS_SUCCESS).build();
        } else {
            response = RegisterResponse.newBuilder().setStatus(STATUS_FAILURE).build();
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void heartBeat(HeartBeatRequest request, StreamObserver<HeartBeatResponse> responseObserver) {

        String ip = request.getIp();
        String hostname = request.getHostname();

        Boolean result = dataNodeManager.heartBeat(ip, hostname);

        HeartBeatResponse response = null;

        List<Command> commandList = new ArrayList<>();

        if (result) {

            DataNodeInfo dataNodeInfo = this.dataNodeManager.getDataNodeInfo(ip, hostname);
            ReplicateTask replicateTask = null;

            while ((replicateTask = dataNodeInfo.pollReplicateTask()) != null) {
                Command replicateCommand = new Command(Command.REPLICATE);
                replicateCommand.setContent(JSONObject.toJSONString(replicateTask));
                commandList.add(replicateCommand);
                System.out.println("commandList-replicaTask: " + replicateCommand);
            }


            RemoveReplicaTask removeReplicaTask = null;

            while ((removeReplicaTask = dataNodeInfo.pollRemoveReplicaTask()) != null) {
                Command removeReplicaCommand = new Command(Command.REMOVE_REPLICA);
                removeReplicaCommand.setContent(JSONObject.toJSONString(removeReplicaTask));
                commandList.add(removeReplicaCommand);
            }

            response = HeartBeatResponse.newBuilder()
                    .setStatus(STATUS_SUCCESS)
                    .setCommands(JSONArray.toJSONString(commandList))
                    .build();
        } else {
            Command registerCommand = new Command(Command.REGISTER);
            Command reportCompleteStorageInfoCommand = new Command(Command.REPORT_COMPLETE_STORAGE_INFO);
            commandList.add(registerCommand);
            commandList.add(reportCompleteStorageInfoCommand);

            response = HeartBeatResponse.newBuilder()
                    .setStatus(STATUS_FAILURE)
                    .setCommands(JSONArray.toJSONString(commandList))
                    .build();
        }

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
        // flush editslog into disk
        this.fsNameSystem.flush();
        this.fsNameSystem.saveCheckpointTxId();
        System.out.println("gracefully shutdown...");
    }

    /**
     * fetch return size <= 10
     */
    @Override
    public void fetchEditsLog(FetchEditsLogRequest request, StreamObserver<FetchEditsLogResponse> responseObserver) {

        if (!isRunning) {
            FetchEditsLogResponse response = FetchEditsLogResponse.newBuilder()
                    .setEditsLog(new JSONArray().toJSONString()).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        long syncedTxId = request.getSyncTxId();
        FetchEditsLogResponse response = null;
        JSONArray fetchedEditsLog = new JSONArray();

        // get already flushed into disk max txId
        List<String> flushedTxIds = this.fsNameSystem.getFsEditLog().getFlushedTxIds();

        // data all in memory cache
        if (flushedTxIds.size() == 0) {
            this.fetchFromBufferedEditsLog(fetchedEditsLog, syncedTxId);

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
                if (existInFlushedFile(bufferedFlushedTxId, syncedTxId)) {
                    fetchFromCurrentBuffer(fetchedEditsLog, syncedTxId);
                } else {
                    String nextFlushedTxId = getNextFlushedTxId(flushedTxIds, bufferedFlushedTxId);
                    // if you want fetched editsLog already flushed into disk file, and not cached into currentBufferedEditsLog -> 1.1 + 1.2
                    if (nextFlushedTxId != null) {
                        fetchFromFlushedFile(nextFlushedTxId, fetchedEditsLog, syncedTxId);
                        // if you want fetched editsLog is in doubleBuffer's currentBuffer -> 1.3
                    } else {
                        fetchFromBufferedEditsLog(fetchedEditsLog, syncedTxId);
                    }
                }

                // if currentBufferedEditsLog hasn't cached any editsLog flushed into disk file
            } else {
                boolean fetchedFromFlushedFile = false;

                for (String flushedTxId : flushedTxIds) {
                    // if you want fetched editsLog file already flushed into disk -> 2.1 + 2.2
                    if (existInFlushedFile(flushedTxId, syncedTxId)) {
                        fetchFromFlushedFile(flushedTxId, fetchedEditsLog, syncedTxId);
                        fetchedFromFlushedFile = true;
                        break;
                    }
                }

                // 2.3
                if (!fetchedFromFlushedFile) {
                    fetchFromBufferedEditsLog(fetchedEditsLog, syncedTxId);
                }
            }
        }

        response = FetchEditsLogResponse.newBuilder().setEditsLog(fetchedEditsLog.toJSONString()).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateCheckpointTxId(UpdateCheckpointTxIdRequest request, StreamObserver<UpdateCheckpointTxIdResponse> responseObserver) {

        long txId = request.getTxId();
        this.fsNameSystem.setCheckpointTxId(txId);

        UpdateCheckpointTxIdResponse response = null;
        response = UpdateCheckpointTxIdResponse.newBuilder().setStatus(1).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void create(CreateFileRequest request, StreamObserver<CreateFileResponse> responseObserver) {
        try {
            CreateFileResponse response = null;
            if (!isRunning) {
                response = CreateFileResponse.newBuilder().setStatus(STATUS_SHUTDOWN).build();
            } else {
                String filename = request.getFilename();
                Boolean success = this.fsNameSystem.create(filename);

                if (success) {
                    response = CreateFileResponse.newBuilder().setStatus(STATUS_SUCCESS).build();
                } else {
                    response = CreateFileResponse.newBuilder().setStatus(STATUS_DUPLICATE).build();
                }
            }
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * allocate dataNodes for file upload
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void allocateDataNodes(AllocateDataNodesRequest request, StreamObserver<AllocateDataNodesResponse> responseObserver) {

        long fileSize = request.getFileSize();
        List<DataNodeInfo> selectedDataNodeInfoList = this.dataNodeManager.allocateDataNodes(fileSize);

        String selectedDataNodeInfoListJson = JSONArray.toJSONString(selectedDataNodeInfoList);

        AllocateDataNodesResponse response = AllocateDataNodesResponse.newBuilder().setDataNodes(selectedDataNodeInfoListJson).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void informReplicaReceived(InformReplicaReceivedRequest request, StreamObserver<InformReplicaReceivedResponse> responseObserver) {

        String hostname = request.getHostname();
        String ip = request.getIp();
        String filename = request.getFilename();

        InformReplicaReceivedResponse response = null;

        try {
            this.fsNameSystem.addReceivedReplica(hostname, ip, filename.split("_")[0], Long.valueOf(filename.split("_")[1]));
            response = InformReplicaReceivedResponse.newBuilder()
                    .setStatus(STATUS_SUCCESS)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            response = InformReplicaReceivedResponse.newBuilder()
                    .setStatus(STATUS_FAILURE)
                    .build();
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void reportAllStorageInfo(ReportAllStorageInfoRequest request, StreamObserver<ReportAllStorageInfoResponse> responseObserver) {

        String ip = request.getIp();
        String hostname = request.getHostname();
        String filenameListJson = request.getFilenameListJson();
        Long storedDataSize = request.getStoredDataSize();

        this.dataNodeManager.setStoredDataSize(ip, hostname, storedDataSize);

        JSONArray filenameJSONArray = JSONArray.parseArray(filenameListJson);

        for (int i = 0; i < filenameJSONArray.size(); i++) {
            String filename = filenameJSONArray.getString(i);
            this.fsNameSystem.addReceivedReplica(hostname, ip, filename.split("_")[0], Long.valueOf(filename.split("_")[1]));
        }

        ReportAllStorageInfoResponse response = ReportAllStorageInfoResponse.newBuilder().setStatus(STATUS_SUCCESS).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * get the datanode where the file is located
     */
    @Override
    public void chooseDataNodeFromReplicas(ChooseDataNodeFromReplicasRequest request, StreamObserver<ChooseDataNodeFromReplicasResponse> responseObserver) {

        String filename = request.getFilename();
        DataNodeInfo dataNodeInfo = this.fsNameSystem.getDataNodeForFile(filename);

        ChooseDataNodeFromReplicasResponse response = ChooseDataNodeFromReplicasResponse.newBuilder()
                .setDataNodeInfo(JSONObject.toJSONString(dataNodeInfo))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void reallocateDataNode(ReallocateDataNodeRequest request, StreamObserver<ReallocateDataNodeResponse> responseObserver) {
        long fileSize = request.getFileSize();
        String excludeDataNodeId = request.getExcludedDataNodeId();
        DataNodeInfo dataNodeInfo = this.dataNodeManager.reallocateDataNode(fileSize, excludeDataNodeId);

        ReallocateDataNodeResponse response = ReallocateDataNodeResponse.newBuilder().setDataNodeInfo(JSONObject.toJSONString(dataNodeInfo)).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    /**
     * @param flushedTxId startTxId_endTxId
     */
    private Boolean existInFlushedFile(String flushedTxId, long syncedTxId) {
        String[] flushedTxIdSplit = flushedTxId.split("_");
        long startTxId = Long.parseLong(flushedTxIdSplit[0]);
        long endTxId = Long.parseLong(flushedTxIdSplit[1]);
        long fetchTxId = syncedTxId + 1;

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
    private void fetchFromFlushedFile(String flushedTxId, JSONArray fetchedEditsLog, long syncedTxId) {
        try {
            String[] flushedTxIdSplit = flushedTxId.split("_");
            long startTxId = Long.parseLong(flushedTxIdSplit[0]);
            long endTxId = Long.parseLong(flushedTxIdSplit[1]);

            String currentEditsLogFile = "E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\editslog\\edits-" + (startTxId) + "-" + endTxId + ".log";
            List<String> editsLog = Files.readAllLines(Paths.get(currentEditsLogFile), StandardCharsets.UTF_8);

            this.currentBufferedEditsLog.clear();
            for (String editLog : editsLog) {
                this.currentBufferedEditsLog.add(JSONObject.parseObject(editLog));
                this.currentBufferedTxId = JSONObject.parseObject(editLog).getLongValue("txId");
            }

            // cached flushed into disk file startTxId_endTxtId which transfer to currentBufferedEditsLog
            this.bufferedFlushedTxId = flushedTxId;

            fetchFromCurrentBuffer(fetchedEditsLog, syncedTxId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * transfer doubleBuffer's current buffer into currentBufferedEditsLog
     */
    private void fetchFromBufferedEditsLog(JSONArray fetchedEditsLog, long syncedTxId) {

        // if you want fetched editsLog txId already exist in currentBufferedEditsLog, direct execute fetchFromCurrentBuffer

        long fetchTxId = syncedTxId + 1;
        if (fetchTxId <= this.currentBufferedTxId) {
            System.out.println("fetch from bufferedEditsLog, already exist in currentBufferedEditsLog");
            fetchFromCurrentBuffer(fetchedEditsLog, syncedTxId);
            return;
        }

        // else transfer editsLog from doubleBuffer to currentBufferedEditsLog, and execute
        this.currentBufferedEditsLog.clear();
        String[] bufferedEditsLog = this.fsNameSystem.getFsEditLog().getBufferedEditsLog();

        if (bufferedEditsLog != null) {
            this.currentBufferedTxId = 0L;
            for (String editLog : bufferedEditsLog) {
                this.currentBufferedEditsLog.add(JSONObject.parseObject(editLog));
                this.currentBufferedTxId = JSONObject.parseObject(editLog).getLongValue("txId");
            }
            this.bufferedFlushedTxId = null;
            fetchFromCurrentBuffer(fetchedEditsLog, syncedTxId);
        }
    }


    /**
     * fetch editsLog data from currentBufferedEditsLog
     */
    private void fetchFromCurrentBuffer(JSONArray fetchedEditsLog, long syncedTxId) {
        int fetchCount = 0;
        long fetchTxId = syncedTxId + 1;
        for (int i = 0; i < this.currentBufferedEditsLog.size(); i++) {
            if (this.currentBufferedEditsLog.getJSONObject(i).getLong("txId") == fetchTxId) {
                fetchedEditsLog.add(this.currentBufferedEditsLog.getJSONObject(i));
                fetchTxId = this.currentBufferedEditsLog.getJSONObject(i).getLongValue("txId") + 1;
                fetchCount++;
            }
            if (fetchCount == BACKUP_NODE_FETCH_SIZE) {
                break;
            }
        }
    }


}
