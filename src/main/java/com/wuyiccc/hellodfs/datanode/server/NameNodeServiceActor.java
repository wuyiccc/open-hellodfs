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
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            latch.countDown();
        }
    }


}
