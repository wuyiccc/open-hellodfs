package com.wuyiccc.netty.demo.nio.nioaction;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;

/**
 * @author wuyiccc
 * @date 2022/6/26 22:34
 */
public class NIOClient {

    private static CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
    private static CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();


    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Worker().start();
        }
    }

    static class Worker extends Thread {
        @Override
        public void run() {
            SocketChannel channel = null;
            Selector selector = null;
            try {
                channel = SocketChannel.open();
                channel.configureBlocking(false);
                // 尝试进行TCP三次握手
                channel.connect(new InetSocketAddress("localhost", 9000));

                selector = Selector.open();
                // 监听connect行为
                channel.register(selector, SelectionKey.OP_CONNECT);

                while (true) {
                    selector.select();
                    Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
                    while (keysIterator.hasNext()) {
                        SelectionKey key = keysIterator.next();
                        keysIterator.remove();

                        if (key.isConnectable()) {
                            channel = (SocketChannel) key.channel();
                            if (channel.isConnectionPending()) {
                                channel.finishConnect();
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                buffer.put("你好".getBytes());
                                buffer.flip();
                                channel.write(buffer);
                            }
                            channel.register(selector, SelectionKey.OP_READ);
                        } else if (key.isReadable()) {
                            channel = (SocketChannel) key.channel();

                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int len = channel.read(buffer);  // 把数据写入buffer，position推进到读取的字节数数字

                            if (len > 0) {
                                System.out.println("[" + Thread.currentThread().getName()
                                        + "]收到响应：" + new String(buffer.array(), 0, len));
                                Thread.sleep(5000);
                                channel.register(selector, SelectionKey.OP_WRITE);
                            }
                        } else {
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            buffer.put("你好".getBytes());
                            buffer.flip();

                            channel = (SocketChannel) key.channel();
                            channel.write(buffer);
                            channel.register(selector, SelectionKey.OP_READ);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (selector != null) {
                    try {
                        selector.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
