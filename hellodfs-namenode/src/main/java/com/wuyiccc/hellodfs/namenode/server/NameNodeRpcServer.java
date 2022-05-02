package com.wuyiccc.hellodfs.namenode.server;

import com.wuyiccc.hellodfs.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * rpc server, provide rpc service
 *
 * @author wuyiccc
 * @date 2022/4/26 22:05
 */
public class NameNodeRpcServer {

    private static final int DEFAULT_PORT = 50070;

    private Server server = null;

    /**
     * manage meta data
     */
    private FSNameSystem fsNameSystem;

    /**
     * manage datanode cluster
     */
    private DataNodeManager dataNodeManager;

    public NameNodeRpcServer(FSNameSystem fsNameSystem, DataNodeManager dataNodeManager) {
        this.fsNameSystem = fsNameSystem;
        this.dataNodeManager = dataNodeManager;
    }

    public void start() throws IOException {
        // start rpc server to listen define port and bind interface
        server = ServerBuilder
                .forPort(DEFAULT_PORT)
                .addService(NameNodeServiceGrpc.bindService(new NameNodeServiceImpl(fsNameSystem, dataNodeManager)))
                .build()
                .start();

        System.out.println("NameNodeRpcServer start, listen port: " + DEFAULT_PORT);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                NameNodeRpcServer.this.stop();
            }
        });
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}
