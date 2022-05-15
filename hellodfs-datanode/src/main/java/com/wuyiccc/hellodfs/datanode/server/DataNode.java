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

    private NameNodeOfferService offerService;


    private void initialize() {
        this.shouldRun = true;

        this.offerService = new NameNodeOfferService();
        this.offerService.start();

        DataNodeNIOServer nioServer = new DataNodeNIOServer();
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


    public static void main(String[] args) {

        DataNode dataNode = new DataNode();
        dataNode.initialize();
        dataNode.run();
    }
}
