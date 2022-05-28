package com.wuyiccc.hellodfs.datanode.server;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author wuyiccc
 * @date 2022/5/28 8:48
 */
public class NIOProcessor extends Thread {


    public static final Long POLL_BLOCK_MAX_TIME = 1000L;


    private Integer processorId;

    private ConcurrentLinkedQueue<SocketChannel> channelQueue = new ConcurrentLinkedQueue<>();

    private Selector selector;

    private Map<String, NetworkRequest> cachedRequests = new HashMap<>();

    private Map<String, NetworkResponse> cachedResponses = new HashMap<>();

    private Map<String, SelectionKey> cachedKeys = new HashMap<>();

    public NIOProcessor(Integer processorId) {
        try {
            this.processorId = processorId;
            this.selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Integer getProcessorId() {
        return processorId;
    }

    public void setProcessorId(Integer processorId) {
        this.processorId = processorId;
    }

    public void addChannel(SocketChannel channel) {
        this.channelQueue.offer(channel);
        selector.wakeup();
    }


    @Override
    public void run() {
        while (true) {
            try {
                registerQueuedClients();
                cacheQueuedResponse();
                poll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void registerQueuedClients() {
        SocketChannel channel = null;
        while ((channel = channelQueue.poll()) != null) {
            try {
                channel.register(selector, SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        }
    }

    private void poll() {
        try {
            int keys = selector.select(POLL_BLOCK_MAX_TIME);

            if (keys > 0) {
                Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    try {
                        SelectionKey key = keyIterator.next();
                        keyIterator.remove();


                        SocketChannel channel = (SocketChannel) key.channel();
                        String client = channel.getRemoteAddress().toString();

                        if (key.isReadable()) {


                            NetworkRequest request = null;
                            if (cachedRequests.get(client) != null) {
                                request = cachedRequests.get(client);
                            } else {
                                request = new NetworkRequest();
                            }

                            request.setChannel(channel);
                            request.setKey(key);
                            request.read();

                            if (request.hasCompletedRead()) {
                                request.setProcessorId(processorId);
                                request.setClient(client);

                                NetworkRequestQueue.getInstance().offer(request);

                                cachedKeys.put(client, key);
                                cachedRequests.remove(client);

                                key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
                            } else {
                                cachedRequests.put(client, request);
                            }
                        } else if (key.isWritable()) {
                            NetworkResponse response = cachedResponses.get(client);
                            channel.write(response.getBuffer());

                            cachedResponses.remove(client);
                            cachedKeys.remove(client);

                            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cacheQueuedResponse() {
        NetworkResponseQueues responseQueues = NetworkResponseQueues.getInstance();
        NetworkResponse response = null;

        while ((response = responseQueues.poll(processorId)) != null) {
            String client = response.getClient();
            cachedResponses.put(client, response);
            cachedKeys.get(client).interestOps(SelectionKey.OP_WRITE);
        }
    }
}
