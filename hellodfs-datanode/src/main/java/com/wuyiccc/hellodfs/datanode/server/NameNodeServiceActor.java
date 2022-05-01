package com.wuyiccc.hellodfs.datanode.server;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Responsible for communicating with a NameNode
 *
 * @author wuyiccc
 * @date 2022/4/30 20:23
 */
public class NameNodeServiceActor {

    /**
     * register itself to which(namenode) communicating
     */
    public void register(CountDownLatch latch) {
        Thread registerThread = new RegisterThread(latch);
        registerThread.start();
    }

    public void startHeartBeat() {
        HeartbeatThread heartbeatThread = new HeartbeatThread();
        heartbeatThread.start();
    }

    /**
     * register thread
     */
    class RegisterThread extends Thread {
        CountDownLatch latch;

        public RegisterThread(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            // send rpc request to namenode for register
            System.out.println("send request to namenode for register...");
            try {

                // TODO: read from settings.properties
                String ip = "127.0.0.1";
                String hostname = "dfs-data-01";
                TimeUnit.SECONDS.sleep(5);
                latch.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    class HeartbeatThread extends Thread {

        @Override
        public void run() {
            try {
                while (true) {
                    System.out.println("send heartbeat request to namenode.......");

                    String ip = "127.0.0.1";
                    String hostname = "dfs-data-01";

                    TimeUnit.SECONDS.sleep(30);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
