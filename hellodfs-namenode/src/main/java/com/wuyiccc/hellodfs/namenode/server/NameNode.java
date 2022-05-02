package com.wuyiccc.hellodfs.namenode.server;

/**
 * The bootstrap class to start NameNode
 *
 * @author wuyiccc
 * @date 2022/4/26 22:19
 */
public class NameNode {

    /**
     * manage meta data
     */
    private FSNameSystem fsNameSystem;

    /**
     * manager datanode cluster
     */
    private DataNodeManager dataNodeManager;

    /**
     * provide rpc service
     */
    private NameNodeRpcServer nameNodeRpcServer;


    /**
     * initialize namenode
     */
    private void initialize() {
        this.fsNameSystem = new FSNameSystem();
        this.dataNodeManager = new DataNodeManager();
        this.nameNodeRpcServer = new NameNodeRpcServer(this.fsNameSystem, this.dataNodeManager);
    }

    private void start() throws Exception {
        this.nameNodeRpcServer.start();
        this.nameNodeRpcServer.blockUntilShutdown();
    }


    public static void main(String[] args) throws Exception {
        NameNode nameNode = new NameNode();
        nameNode.initialize();
        nameNode.start();
    }

}
