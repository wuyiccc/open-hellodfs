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

    private void initialize() throws Exception{
        this.shouldRun = true;

        this.nameNodeRpcClient = new NameNodeRpcClient();
        this.nameNodeRpcClient.start();

        DataNodeNIOServer nioServer = new DataNodeNIOServer(this.nameNodeRpcClient);
        nioServer.start();
    }

    private void run() {
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
        dataNode.initialize();
        dataNode.run();
    }
}
