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
    private NameNodeServiceActor serviceActor;

    /**
     * save service actor list
     */
    private CopyOnWriteArrayList<NameNodeServiceActor> serviceActors;

    public NameNodeOfferService() {
        this.serviceActor = new NameNodeServiceActor();
    }

    /**
     * start offerService Component
     */
    public void start() {
        // start to register to namenode
        register();
        // start to send heartbeat to namenode
        startHeartBeat();
    }

    private void startHeartBeat() {
        this.serviceActor.startHeartBeat();
    }

    /**
     * register itself to namenode(active and backup)
     */
    private void register() {
        try {
            serviceActor.register();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * close serviceActor
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
