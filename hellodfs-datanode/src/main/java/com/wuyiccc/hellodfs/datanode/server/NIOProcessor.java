package com.wuyiccc.hellodfs.datanode.server;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author wuyiccc
 * @date 2022/5/28 8:48
 */
public class NIOProcessor extends Thread {


    private ConcurrentLinkedQueue<SocketChannel> channelQueue = new ConcurrentLinkedQueue<>();

    private Selector selector;

    public NIOProcessor() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addChannel(SocketChannel channel) {
        this.channelQueue.offer(channel);
    }


    @Override
    public void run() {
        while (true) {
            try {
                registerQueuedClients();
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
}
