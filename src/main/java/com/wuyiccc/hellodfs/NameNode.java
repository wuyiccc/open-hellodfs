package com.wuyiccc.hellodfs;

import javax.crypto.MacSpi;
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

    public NameNode() {
        this.shouldRun = true;
    }

    /**
     * initialize namenode
     */
    private void initialize() {
        this.fsNameSystem = new FSNameSystem();
        this.nameNodeRpcServer = new NameNodeRpcServer(this.fsNameSystem);
        this.nameNodeRpcServer.start();
    }

    private void run() throws InterruptedException {
        try {
            while (shouldRun) {
                TimeUnit.SECONDS.sleep(10);
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
