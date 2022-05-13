package com.wuyiccc.hellodfs.backupnode.server;

import com.alibaba.fastjson.JSONArray;
import com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogRequest;
import com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogResponse;
import com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdRequest;
import com.wuyiccc.hellodfs.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;

/**
 * @author wuyiccc
 * @date 2022/5/6 23:12
 */
public class NameNodeRpcClient {

    private static final String NAME_NODE_HOSTNAME = "localhost";
    private static final Integer NAME_NODE_PORT = 50070;

    private NameNodeServiceGrpc.NameNodeServiceBlockingStub nameNode;

    private Boolean isNameNodeRunning = true;


    public NameNodeRpcClient() {
        ManagedChannel channel = NettyChannelBuilder.forAddress(NAME_NODE_HOSTNAME, NAME_NODE_PORT).negotiationType(NegotiationType.PLAINTEXT).build();
        this.nameNode = NameNodeServiceGrpc.newBlockingStub(channel);
    }

    public JSONArray fetchEditsLog(long syncTxId) {
        FetchEditsLogRequest request = FetchEditsLogRequest.newBuilder().setSyncTxId(syncTxId).build();

        FetchEditsLogResponse response = this.nameNode.fetchEditsLog(request);

        String editsLogJson = response.getEditsLog();
        return JSONArray.parseArray(editsLogJson);
    }

    public void updateCheckpointTxId(long txId) {
        UpdateCheckpointTxIdRequest request = UpdateCheckpointTxIdRequest.newBuilder().setTxId(txId).build();

        this.nameNode.updateCheckpointTxId(request);
    }

    public Boolean isNameNodeRunning() {
        return isNameNodeRunning;
    }

    public void setIsNameNodeRunning(Boolean nameNodeRunning) {
        isNameNodeRunning = nameNodeRunning;
    }
}
