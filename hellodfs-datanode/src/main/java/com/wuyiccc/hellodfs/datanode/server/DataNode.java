package com.wuyiccc.hellodfs.datanode.server;

import java.util.concurrent.TimeUnit;

/**
 * datanode bootstrap class node
 *
 * @author wuyiccc
 * @date 2022/4/30 20:21
 */
public class DataNode {

    private volatile Boolean shouldRun;


    private NameNodeRpcClient nameNodeRpcClient;

    private HeartBeatManager heartBeatManager;

    private StorageManager storageManager;

    private ReplicateManager replicateManager;

    private DataNode() throws Exception {
        this.shouldRun = true;

        this.nameNodeRpcClient = new NameNodeRpcClient();
        Boolean res = this.nameNodeRpcClient.register();

        this.storageManager = new StorageManager();

        if (res) {
            StorageInfo storageInfo = this.storageManager.getStorageInfo();
            this.nameNodeRpcClient.reportAllStorageInfo(storageInfo);
        } else {
            System.out.println("already register, don't need to report all storage info...");
        }

        this.replicateManager = new ReplicateManager(this.nameNodeRpcClient);

        this.heartBeatManager = new HeartBeatManager(this.nameNodeRpcClient, this.storageManager, this.replicateManager);
        this.heartBeatManager.start();

        NIOServer nioServer = new NIOServer(this.nameNodeRpcClient);
        nioServer.init();
        nioServer.start();
    }


    private void start() {
        try {
            while (shouldRun) {
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        DataNode dataNode = new DataNode();
        dataNode.start();
    }
}
