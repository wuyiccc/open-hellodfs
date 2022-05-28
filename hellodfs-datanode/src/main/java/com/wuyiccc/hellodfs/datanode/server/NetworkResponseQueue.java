package com.wuyiccc.hellodfs.datanode.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author wuyiccc
 * @date 2022/5/28 14:49
 */
public class NetworkResponseQueue {


    private Map<Integer, ConcurrentLinkedQueue<NetworkResponse>> responseQueues = new HashMap<>();

    private static volatile NetworkResponseQueue instance = null;

    public static NetworkResponseQueue getInstance() {
        if (instance == null) {
            synchronized (NetworkResponseQueue.class) {
                if (instance == null) {
                    instance = new NetworkResponseQueue();
                }
            }
        }
        return instance;
    }


    public void initResponseQueue(Integer processorId) {
        ConcurrentLinkedQueue<NetworkResponse> responseQueue = new ConcurrentLinkedQueue<>();
        responseQueues.put(processorId, responseQueue);
    }

    public void offer(Integer processorId, NetworkResponse response) {
        responseQueues.get(processorId).offer(response);
    }

}
