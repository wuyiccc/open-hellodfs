package com.wuyiccc.hellodfs.datanode.server;

import com.alibaba.fastjson.JSONObject;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author wuyiccc
 * @date 2022/5/23 22:33
 */
public class ReplicateManager {


    public static final Integer REPLICATE_THREAD_NUM = 3;

    private ConcurrentLinkedQueue<JSONObject> replicateTaskQueue = new ConcurrentLinkedQueue<>();

    public ReplicateManager() {
        for (int i = 0; i < REPLICATE_THREAD_NUM; i++) {
            new ReplicateWorker().start();
        }
    }


    public void addReplicateTask(JSONObject replicateTask) {
        replicateTaskQueue.offer(replicateTask);
    }

    class ReplicateWorker extends Thread {
        @Override
        public void run() {

            while (true) {
                try {
                    JSONObject replicateTask = replicateTaskQueue.poll();
                    if (replicateTask == null) {
                        TimeUnit.SECONDS.sleep(1);
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
