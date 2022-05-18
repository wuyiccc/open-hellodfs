package com.wuyiccc.hellodfs.datanode.server;

import sun.plugin2.message.HeartbeatMessage;

import java.io.File;
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

    private DataNode() throws Exception {
        this.shouldRun = true;

        this.nameNodeRpcClient = new NameNodeRpcClient();
        Boolean res = this.nameNodeRpcClient.register();

        if (!res) {
            System.out.println("register to namenode failure");
            System.exit(1);
        }

        this.storageManager = new StorageManager();
        this.heartBeatManager = new HeartBeatManager(this.nameNodeRpcClient, this.storageManager);
        this.heartBeatManager.start();

        StorageInfo storageInfo = this.storageManager.getStorageInfo();
        if (storageInfo != null) {
            this.nameNodeRpcClient.reportAllStorageInfo(storageInfo);
        }

        DataNodeNIOServer nioServer = new DataNodeNIOServer(this.nameNodeRpcClient);
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
