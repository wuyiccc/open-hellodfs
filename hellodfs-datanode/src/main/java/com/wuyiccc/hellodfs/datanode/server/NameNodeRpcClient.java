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
        ManagedChannel channel = NettyChannelBuilder.forAddress(DataNodeConfig.NAMENODE_HOSTNAME, DataNodeConfig.NAMENODE_PORT)
                .negotiationType(NegotiationType.PLAINTEXT)
                .build();
        this.nameNode = NameNodeServiceGrpc.newBlockingStub(channel);
    }


    /**
     * send register request to NameNode which bind
     */
    public void register() throws Exception {

        System.out.println("send rpc request to namenode for register.......");

        RegisterRequest request = RegisterRequest.newBuilder()
                .setIp(DataNodeConfig.DATANODE_IP)
                .setHostname(DataNodeConfig.DATANODE_HOSTNAME)
                .setNioPort(DataNodeConfig.NIO_PORT)
                .build();
        RegisterResponse response = this.nameNode.register(request);

        System.out.println("register thread accept namenode response data" + response.getStatus());
    }

    /**
     * send heartbeat request to NameNode per 30s
     */
    public void startHeartBeat() {
        new HeartBeatThread().start();
    }

    public void reportAllStorageInfo(StorageInfo storageInfo) {
        ReportAllStorageInfoRequest request = ReportAllStorageInfoRequest.newBuilder()
                .setIp(DataNodeConfig.DATANODE_IP)
                .setHostname(DataNodeConfig.DATANODE_HOSTNAME)
                .setFilenameListJson(JSONArray.toJSONString(storageInfo.getFilenameList()))
                .setStoredDataSize(storageInfo.getStoredDataSize())
                .build();
        this.nameNode.reportAllStorageInfo(request);
    }

    /**
     * notify the master that it has received a replica data
     */
    public void informReplicaReceived(String filename) throws Exception {
        InformReplicaReceivedRequest request = InformReplicaReceivedRequest.newBuilder()
                .setHostname(DataNodeConfig.DATANODE_HOSTNAME)
                .setIp(DataNodeConfig.DATANODE_IP)
                .setFilename(filename)
                .build();
        this.nameNode.informReplicaReceived(request);
    }

    /**
     * send heartbeat to namenode thread
     */
    class HeartBeatThread extends Thread {

        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println("send rpc request to namenode for heartbeat.......");

                    HeartBeatRequest request = HeartBeatRequest.newBuilder()
                            .setIp(DataNodeConfig.DATANODE_IP)
                            .setHostname(DataNodeConfig.DATANODE_HOSTNAME)
                            .build();
                    HeartBeatResponse response = nameNode.heartBeat(request);

                    System.out.println("heartbeat thread accept namenode response data: " + response.getStatus());

                    TimeUnit.SECONDS.sleep(30);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
