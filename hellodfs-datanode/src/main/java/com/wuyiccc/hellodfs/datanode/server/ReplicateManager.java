package com.wuyiccc.hellodfs.datanode.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author wuyiccc
 * @date 2022/5/23 22:33
 */
public class ReplicateManager {

    private ConcurrentLinkedQueue<JSONObject> replicateTaskQueue = new ConcurrentLinkedQueue<>();

    public void addReplicateTask(JSONObject replicateTask) {
        replicateTaskQueue.offer(replicateTask);
    }
}
