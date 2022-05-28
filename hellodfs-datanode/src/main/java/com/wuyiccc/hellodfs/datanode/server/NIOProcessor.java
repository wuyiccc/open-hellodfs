package com.wuyiccc.hellodfs.datanode.server;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author wuyiccc
 * @date 2022/5/28 8:48
 */
public class NIOProcessor extends Thread {


    public static final Long POLL_BLOCK_MAX_TIME = 1000L;

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
        selector.wakeup();
    }


    @Override
    public void run() {
        while (true) {
            try {
                registerQueuedClients();
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
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
