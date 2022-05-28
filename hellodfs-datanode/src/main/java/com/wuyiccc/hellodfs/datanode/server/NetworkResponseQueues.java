package com.wuyiccc.hellodfs.datanode.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author wuyiccc
 * @date 2022/5/28 14:49
 */
public class NetworkResponseQueues {


    private Map<Integer, ConcurrentLinkedQueue<NetworkResponse>> responseQueues = new HashMap<>();

    private static volatile NetworkResponseQueues instance = null;

    public static NetworkResponseQueues getInstance() {
        if (instance == null) {
            synchronized (NetworkResponseQueues.class) {
                if (instance == null) {
                    instance = new NetworkResponseQueues();
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

    public NetworkResponse poll(Integer processorId) {
        return responseQueues.get(processorId).poll();
    }

}
