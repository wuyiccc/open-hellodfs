package com.wuyiccc.hellodfs.namenode.server;

import java.util.concurrent.TimeUnit;

/**
 * The bootstrap class to start NameNode
 *
 * @author wuyiccc
 * @date 2022/4/26 22:19
 */
public class NameNode {

    /**
     * mark the NameNode thread status
     */
    private volatile Boolean shouldRun;

    /**
     * manage meta data
     */
    private FSNameSystem fsNameSystem;

    /**
     * provide rpc service
     */
    private NameNodeRpcServer nameNodeRpcServer;

    /**
     * manager datanode cluster
     */
    private DataNodeManager dataNodeManager;

    public NameNode() {
        this.shouldRun = true;
    }

    /**
     * initialize namenode
     */
    private void initialize() {
        this.fsNameSystem = new FSNameSystem();
        this.dataNodeManager = new DataNodeManager();
        this.nameNodeRpcServer = new NameNodeRpcServer(this.fsNameSystem, this.dataNodeManager);
        this.nameNodeRpcServer.start();
    }

    private void run() throws InterruptedException {
        try {
            while (shouldRun) {
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        NameNode nameNode = new NameNode();
        nameNode.initialize();
        nameNode.run();
    }

}
