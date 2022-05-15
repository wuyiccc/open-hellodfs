package com.wuyiccc.hellodfs.datanode.server;

import com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatRequest;
import com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatResponse;
import com.wuyiccc.hellodfs.namenode.rpc.model.RegisterRequest;
import com.wuyiccc.hellodfs.namenode.rpc.model.RegisterResponse;
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
public class NameNodeServiceActor {

    private static final String NAME_NODE_HOSTNAME = "localhost";
    private static final Integer NAME_NODE_PORT = 50070;

    private NameNodeServiceGrpc.NameNodeServiceBlockingStub nameNode;


    public NameNodeServiceActor() {
        ManagedChannel channel = NettyChannelBuilder.forAddress(NAME_NODE_HOSTNAME, NAME_NODE_PORT).negotiationType(NegotiationType.PLAINTEXT).build();
        this.nameNode = NameNodeServiceGrpc.newBlockingStub(channel);
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
     * per 30s
     * send register request thread
     */
    class RegisterThread extends Thread {

        @Override
        public void run() {
            try {
                System.out.println("send rpc request to namenode for register.......");

                String ip = "127.0.0.2";
                String hostname = "dfs-data-02";

                RegisterRequest request = RegisterRequest.newBuilder().setIp(ip).setHostname(hostname).build();
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

                    String ip = "127.0.0.2";
                    String hostname = "dfs-data-02";

                    HeartBeatRequest request = HeartBeatRequest.newBuilder().setIp(ip).setHostname(hostname).build();
                    HeartBeatResponse response = nameNode.heartBeat(request);

                    System.out.println("hearbeat thread accept namnode response data: " + response.getStatus());

                    TimeUnit.SECONDS.sleep(30);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
