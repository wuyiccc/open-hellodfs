package com.wuyiccc.hellodfs.datanode.server;

import com.alibaba.fastjson.JSONArray;
import com.wuyiccc.hellodfs.namenode.rpc.model.*;
import com.wuyiccc.hellodfs.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Responsible for communicating with a NameNode
 *
 * @author wuyiccc
 * @date 2022/4/30 20:23
 */
public class NameNodeRpcClient {


    private NameNodeServiceGrpc.NameNodeServiceBlockingStub nameNode;


    public NameNodeRpcClient() {
        ManagedChannel channel = NettyChannelBuilder.forAddress(DataNodeConfig.getInstance().NAMENODE_HOSTNAME, DataNodeConfig.getInstance().NAMENODE_PORT)
                .negotiationType(NegotiationType.PLAINTEXT)
                .build();
        this.nameNode = NameNodeServiceGrpc.newBlockingStub(channel);
    }


    /**
     * send register request to NameNode which bind
     */
    public Boolean register() throws Exception {

        System.out.println("send rpc request to namenode for register.......");

        RegisterRequest request = RegisterRequest.newBuilder()
                .setIp(DataNodeConfig.getInstance().DATANODE_IP)
                .setHostname(DataNodeConfig.getInstance().DATANODE_HOSTNAME)
                .setNioPort(DataNodeConfig.getInstance().NIO_PORT)
                .build();
        RegisterResponse response = this.nameNode.register(request);

        System.out.println("register thread accept namenode response data" + response.getStatus());

        if (response.getStatus() == 1) {
            return true;
        } else {
            return false;
        }
    }

    public HeartBeatResponse heartBeat() throws Exception{
        HeartBeatRequest request = HeartBeatRequest.newBuilder()
                .setIp(DataNodeConfig.getInstance().DATANODE_IP)
                .setHostname(DataNodeConfig.getInstance().DATANODE_HOSTNAME)
                .setNioPort(DataNodeConfig.getInstance().NIO_PORT)
                .build();
        return this.nameNode.heartBeat(request);
    }

    public void reportAllStorageInfo(StorageInfo storageInfo) {

        if (storageInfo == null) {
            System.out.println("the current datanode has no stored files, all report is not required");
            return;
        }

        ReportAllStorageInfoRequest request = ReportAllStorageInfoRequest.newBuilder()
                .setIp(DataNodeConfig.getInstance().DATANODE_IP)
                .setHostname(DataNodeConfig.getInstance().DATANODE_HOSTNAME)
                .setFilenameListJson(JSONArray.toJSONString(storageInfo.getFilenameList()))
                .setStoredDataSize(storageInfo.getStoredDataSize())
                .build();
        this.nameNode.reportAllStorageInfo(request);
        System.out.println("reportAllStorageInfo: " + storageInfo);
    }

    /**
     * notify the master that it has received a replica data
     */
    public void informReplicaReceived(String filename) throws Exception {
        InformReplicaReceivedRequest request = InformReplicaReceivedRequest.newBuilder()
                .setHostname(DataNodeConfig.getInstance().DATANODE_HOSTNAME)
                .setIp(DataNodeConfig.getInstance().DATANODE_IP)
                .setFilename(filename)
                .build();
        this.nameNode.informReplicaReceived(request);
    }



}
