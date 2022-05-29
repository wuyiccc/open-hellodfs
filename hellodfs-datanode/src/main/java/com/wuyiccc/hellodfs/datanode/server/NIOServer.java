package com.wuyiccc.hellodfs.datanode.server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * @author wuyiccc
 * @date 2022/5/15 11:31
 */
public class NIOServer extends Thread {

    public static final Integer PROCESSOR_THREAD_NUM = 10;

    public static final Integer IO_THREAD_NUM = 10;

    private Selector selector;

    private List<NIOProcessor> processorList = new ArrayList<>();

    private NameNodeRpcClient nameNodeRpcClient;


    public NIOServer(NameNodeRpcClient nameNodeRpcClient) {
        this.nameNodeRpcClient = nameNodeRpcClient;
    }

    public void init() {
        ServerSocketChannel serverSocketChannel = null;
        try {

            selector = Selector.open();

            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(DataNodeConfig.getInstance().NIO_PORT), 100);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);


            System.out.println("NIOServer is starting, begin to listen port：" + DataNodeConfig.getInstance().NIO_PORT);

            NetworkResponseQueues responseQueue = NetworkResponseQueues.getInstance();

            for (int i = 0; i < PROCESSOR_THREAD_NUM; i++) {
                NIOProcessor processor = new NIOProcessor(i);
                processorList.add(processor);
                processor.start();
                responseQueue.initResponseQueue(i);
            }

            for (int i = 0; i < IO_THREAD_NUM; i++) {
                new IOThread(this.nameNodeRpcClient).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        while (true) {
            try {
                selector.select();
                Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();

                while (keysIterator.hasNext()) {
                    SelectionKey key = keysIterator.next();
                    keysIterator.remove();

                    if (key.isAcceptable()) {
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                        SocketChannel channel = serverSocketChannel.accept();
                        if (channel != null) {
                            channel.configureBlocking(false);

                            int processorIndex = new Random().nextInt(PROCESSOR_THREAD_NUM);
                            NIOProcessor processor = processorList.get(processorIndex);
                            processor.addChannel(channel);
                        }
                    }

                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

}
