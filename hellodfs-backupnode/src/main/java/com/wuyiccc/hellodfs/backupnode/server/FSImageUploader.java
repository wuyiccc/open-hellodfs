package com.wuyiccc.hellodfs.backupnode.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author wuyiccc
 * @date 2022/5/10 23:07
 */
public class FSImageUploader extends Thread {

    private FSImage fsImage;

    public FSImageUploader(FSImage fsImage) {
        this.fsImage = fsImage;
    }

    @Override
    public void run() {
        SocketChannel channel = null;
        Selector selector = null;
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress("localhost", 9000));

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_CONNECT);

            boolean uploading = true;

            while (uploading) {
                selector.select();

                Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
                while (keysIterator.hasNext()) {
                    SelectionKey key = keysIterator.next();
                    keysIterator.remove();

                    if (key.isConnectable()) {
                        channel = (SocketChannel) key.channel();

                        if (channel.isConnectionPending()) {
                            channel.finishConnect();
                            ByteBuffer buffer = ByteBuffer.wrap(fsImage.getFsImageJson().getBytes());
                            channel.write(buffer);
                        }

                        channel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
                        channel = (SocketChannel) key.channel();
                        int count = channel.read(buffer);

                        if (count > 0) {
                            System.out.println("upload fsImage file success, return data is: " + new String(buffer.array(), 0, count));
                            channel.close();
                            uploading = false;
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
