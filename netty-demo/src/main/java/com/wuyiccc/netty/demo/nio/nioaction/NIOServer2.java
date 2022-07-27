package com.wuyiccc.netty.demo.nio.nioaction;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author wuyiccc
 * @date 2022/6/26 21:24
 */
public class NIOServer2 {

    private static CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
    private static CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

    private static ByteBuffer readBuffer;
    private static Selector selector;
    private static LinkedBlockingDeque<SelectionKey> requestQueue;
    private static ExecutorService threadPool;

    public static void main(String[] args) {
        init();
        listen();
    }

    private static void init() {

        readBuffer = ByteBuffer.allocate(1024);
        ServerSocketChannel serverSocketChannel = null;

        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            // NIO支持非阻塞的
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(9000), 100);
            // 仅仅关注ServerSocketChannel接收到的OP_ACCEPT连接请求
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        requestQueue = new LinkedBlockingDeque<>(500);
        threadPool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            threadPool.submit(new Worker());
        }

    }

    private static void listen() {
        while (true) {
            try {
                selector.select();
                Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
                while (keysIterator.hasNext()) {
                    SelectionKey key = keysIterator.next();
                    // 每个SelectionKey相当于是一个请求
                    keysIterator.remove();
                    handleRequest(key);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void handleRequest(SelectionKey key) throws IOException {

        SocketChannel channel = null;

        try {
            // 如果是连接请求
            if (key.isAcceptable()) {
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                // 调用accept方法, 进行TCP三次握手
                channel = serverSocketChannel.accept();
                // 如果三次握手成功之后, 就可以获取一个建立好TCP连接的SocketChannel
                // 这个SocketChannel大概可以理解为一个Socket, 是跟客户端进行连接的, 这个SocketChannel就是连通到那个Socket上去, 负责进行网络数据的读写
                channel.configureBlocking(false);
                // 将该SocketChannel注册到Selector中, 仅监听OP_READ请求
                channel.register(selector, SelectionKey.OP_READ);
            } else if (key.isReadable()) {
                channel = (SocketChannel) key.channel();
                // 清空缓存
                readBuffer.clear();
                int count = channel.read(readBuffer);
                if (count > 0) {
                    readBuffer.flip();
                    CharBuffer charBuffer = decoder.decode(readBuffer);
                    String request = charBuffer.toString();
                    System.out.println("服务端接收请求: " + request);
                    channel.write(encoder.encode(CharBuffer.wrap("收到".toCharArray())));
                } else {
                    channel.close();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (channel != null) {
                channel.close();
            }
        }
    }

    static class Worker implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    SelectionKey key = requestQueue.take();
                    handleRequest(key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleRequest(SelectionKey key) throws IOException {

            SocketChannel channel = null;

            try {
                // 如果是连接请求
                if (key.isAcceptable()) {
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                    // 调用accept方法, 进行TCP三次握手
                    channel = serverSocketChannel.accept();
                    // 如果三次握手成功之后, 就可以获取一个建立好TCP连接的SocketChannel
                    // 这个SocketChannel大概可以理解为一个Socket, 是跟客户端进行连接的, 这个SocketChannel就是连通到那个Socket上去, 负责进行网络数据的读写
                    channel.configureBlocking(false);
                    // 将该SocketChannel注册到Selector中, 仅监听OP_READ请求
                    channel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    channel = (SocketChannel) key.channel();
                    // 清空缓存
                    readBuffer.clear();
                    int count = channel.read(readBuffer);
                    if (count > 0) {
                        readBuffer.flip();
                        CharBuffer charBuffer = decoder.decode(readBuffer);
                        String request = charBuffer.toString();
                        System.out.println("服务端接收请求: " + request);
                        channel.write(encoder.encode(CharBuffer.wrap("收到".toCharArray())));
                    } else {
                        channel.close();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (channel != null) {
                    channel.close();
                }
            }
        }
    }
}
