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

    private static final String NAMENODE_HOSTNAME = "localhost";
    private static final Integer NAMENODE_PORT = 50070;

    private NameNodeServiceGrpc.NameNodeServiceBlockingStub namenode;


    public NameNodeServiceActor() {
        ManagedChannel channel = NettyChannelBuilder.forAddress(NAMENODE_HOSTNAME, NAMENODE_PORT).negotiationType(NegotiationType.PLAINTEXT).build();
        this.namenode = NameNodeServiceGrpc.newBlockingStub(channel);
    }

    /**
     * send register request to namenode which bind
     */
    public void register() throws Exception {
        Thread registerThread = new RegisterThread();
        registerThread.start();
        // wait thread finish
        registerThread.join();
    }

    /**
     * send heartbeat request to namenode per 30s
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

                String ip = "127.0.0.1";
                String hostname = "dfs-data-01";

                RegisterRequest request = RegisterRequest.newBuilder().setIp(ip).setHostname(hostname).build();
                RegisterResponse response = namenode.register(request);

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

                    String ip = "127.0.0.1";
                    String hostname = "dfs-data-01";

                    HeartBeatRequest request = HeartBeatRequest.newBuilder().setIp(ip).setHostname(hostname).build();
                    HeartBeatResponse response = namenode.heartBeat(request);

                    System.out.println("hearbeat thread accept namnode response data: " + response.getStatus());

                    TimeUnit.SECONDS.sleep(30);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
