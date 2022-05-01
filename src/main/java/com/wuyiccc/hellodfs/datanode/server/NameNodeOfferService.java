package com.wuyiccc.hellodfs.datanode.server;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Responsible for communicating with a group NameNode(Active + BackUp)
 *
 * @author wuyiccc
 * @date 2022/4/30 20:22
 */
public class NameNodeOfferService {


    /**
     * communicating with master namenode
     */
    private NameNodeServiceActor activeServiceActor;

    /**
     * communicating with backup namenode
     */
    private NameNodeServiceActor standByServiceActor;

    /**
     * save service actor list
     */
    private CopyOnWriteArrayList<NameNodeServiceActor> serviceActors;

    public NameNodeOfferService() {
        this.activeServiceActor = new NameNodeServiceActor();
        this.standByServiceActor = new NameNodeServiceActor();

        this.serviceActors = new CopyOnWriteArrayList<>();
        this.serviceActors.add(this.activeServiceActor);
        this.serviceActors.add(this.standByServiceActor);
    }

    /**
     * start offerService Component
     */
    public void start() {
        // start to register to namenode
        register();
        startHeartBeat();
    }

    private void startHeartBeat() {

        this.activeServiceActor.startHeartBeat();
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


    /**
     * close serviceActor
     * @param serviceActor
     */
    public void shutdown(NameNodeServiceActor serviceActor) {
        this.serviceActors.remove(serviceActor);
    }

    /**
     * itr serviceActor
     */
    public void iteratorActors() {
        Iterator<NameNodeServiceActor> iterator = this.serviceActors.iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
    }
}
