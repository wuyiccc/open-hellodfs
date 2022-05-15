package com.wuyiccc.hellodfs.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author wuyiccc
 * @date 2022/5/15 11:34
 */
public class NIOClient {

    public static void sendFile(String hostname, int nioPort, byte[] file, long fileSize) {
        SocketChannel channel = null;
        Selector selector = null;
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(hostname, nioPort));
            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_CONNECT);

            boolean sending = true;

            while (sending) {
                selector.select();

                Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
                while (keysIterator.hasNext()) {
                    SelectionKey key = keysIterator.next();
                    keysIterator.remove();

                    if (key.isConnectable()) {
                        channel = (SocketChannel) key.channel();

                        if (channel.isConnectionPending()) {
                            channel.finishConnect();

                            long imageLength = fileSize;

                            ByteBuffer buffer = ByteBuffer.allocate((int) imageLength * 2);
                            // set fileSize in transport stream header, the long type need 8 bytes
                            buffer.putLong(imageLength);
                            buffer.put(file);

                            int sentData = channel.write(buffer);

                            System.out.println("already sent: " + sentData + " bytes data");

                            channel.register(selector, SelectionKey.OP_READ);
                        }
                    } else if (key.isReadable()) {
                        channel = (SocketChannel) key.channel();

                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int len = channel.read(buffer);

                        if (len > 0) {
                            System.out.println("[" + Thread.currentThread().getName()
                                    + "]receive response：" + new String(buffer.array(), 0, len));
                            sending = false;
                        }
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
