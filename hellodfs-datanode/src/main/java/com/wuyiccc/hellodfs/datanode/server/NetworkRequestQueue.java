package com.wuyiccc.hellodfs.datanode.server;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author wuyiccc
 * @date 2022/5/28 13:58
 */
public class NetworkRequestQueue {


    private ConcurrentLinkedQueue<NetworkRequest> requestQueue = new ConcurrentLinkedQueue<>();

    private static volatile NetworkRequestQueue instance = null;

    public static NetworkRequestQueue getInstance() {
        if (instance == null) {
            synchronized (NetworkRequestQueue.class) {
                if (instance == null) {
                    instance = new NetworkRequestQueue();
                }
            }
        }
        return instance;
    }


    public void offer(NetworkRequest request) {
        requestQueue.offer(request);
    }

    public NetworkRequest poll() {
        return requestQueue.poll();
    }

}
