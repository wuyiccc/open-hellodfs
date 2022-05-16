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

    private FSImageUploadServer fsImageUploadServer;

    /**
     * initialize namenode
     */
    public NameNode() {
        this.dataNodeManager = new DataNodeManager();
        this.fsNameSystem = new FSNameSystem(this.dataNodeManager);
        this.nameNodeRpcServer = new NameNodeRpcServer(this.fsNameSystem, this.dataNodeManager);
        this.fsImageUploadServer = new FSImageUploadServer();
    }

    private void start() throws Exception {
        this.fsImageUploadServer.start();
        this.nameNodeRpcServer.start();
        this.nameNodeRpcServer.blockUntilShutdown();
    }


    public static void main(String[] args) throws Exception {
        NameNode nameNode = new NameNode();
        nameNode.start();
    }

}
