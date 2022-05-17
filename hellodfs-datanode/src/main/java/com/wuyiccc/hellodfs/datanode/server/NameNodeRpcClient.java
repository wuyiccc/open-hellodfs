package com.wuyiccc.hellodfs.datanode.server;

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
        ManagedChannel channel = NettyChannelBuilder.forAddress(DataNodeConfig.NAMENODE_HOSTNAME, DataNodeConfig.NAMENODE_PORT).negotiationType(NegotiationType.PLAINTEXT).build();
        this.nameNode = NameNodeServiceGrpc.newBlockingStub(channel);
    }


    public void start() throws Exception {
        register();
        startHeartBeat();
    }

    /**
     * send register request to NameNode which bind
     */
    public void register() throws Exception {
        Thread registerThread = new RegisterThread();
        registerThread.start();
        // wait thread finish
        registerThread.join();
    }

    /**
     * send heartbeat request to NameNode per 30s
     */
    public void startHeartBeat() {
        new HeartBeatThread().start();
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
     * per 30s
     * send register request thread
     */
    class RegisterThread extends Thread {

        @Override
        public void run() {
            try {
                System.out.println("send rpc request to namenode for register.......");

                String ip = DataNodeConfig.DATANODE_IP;
                String hostname = DataNodeConfig.DATANODE_HOSTNAME;

                RegisterRequest request = RegisterRequest.newBuilder()
                        .setIp(ip)
                        .setHostname(hostname)
                        .setNioPort(DataNodeConfig.NIO_PORT)
                        .build();
                RegisterResponse response = nameNode.register(request);

                System.out.println("register thread accept namenode response data" + response.getStatus());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

                    String ip = DataNodeConfig.DATANODE_IP;
                    String hostname = DataNodeConfig.DATANODE_HOSTNAME;

                    HeartBeatRequest request = HeartBeatRequest.newBuilder().setIp(ip).setHostname(hostname).build();
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
