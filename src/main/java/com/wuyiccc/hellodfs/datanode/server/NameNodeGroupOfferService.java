package com.wuyiccc.hellodfs.datanode.server;

import java.util.concurrent.CountDownLatch;

/**
 * Responsible for communicating with a group NameNode(Active + BackUp)
 *
 * @author wuyiccc
 * @date 2022/4/30 20:22
 */
public class NameNodeGroupOfferService {


    /**
     * communicating with master namenode
     */
    private NameNodeServiceActor activeServiceActor;

    /**
     * communicating with backup namenode
     */
    private NameNodeServiceActor standByServiceActor;

    public NameNodeGroupOfferService() {
        this.activeServiceActor = new NameNodeServiceActor();
        this.standByServiceActor = new NameNodeServiceActor();
    }

    /**
     * start offerService Component
     */
    public void start() {
        // start to register to namenode
        register();
    }

    /**
     * register itself to namenode(active and backup)
     */
    private void register() {
        try {
            CountDownLatch latch = new CountDownLatch(2);
            activeServiceActor.register(latch);
            standByServiceActor.register(latch);
            latch.await();
            System.out.println("active namenode and backup namenode register success...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
