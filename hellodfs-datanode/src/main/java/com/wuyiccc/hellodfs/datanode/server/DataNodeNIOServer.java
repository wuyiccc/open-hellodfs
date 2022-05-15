package com.wuyiccc.hellodfs.datanode.server;


import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author wuyiccc
 * @date 2022/5/15 11:31
 */
public class DataNodeNIOServer extends Thread {

    private Selector selector;
    private List<LinkedBlockingQueue<SelectionKey>> queueList = new ArrayList<>();
    private Map<String, CachedImage> cachedImageMap = new HashMap<>();


    public DataNodeNIOServer() {
        ServerSocketChannel serverSocketChannel = null;

        try {
            selector = Selector.open();

            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(9000), 100);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            // 1. register 3 queues and worker threads to listen for connections respectively
            // 1.1 register 3 queues
            for (int i = 0; i < 3; i++) {
                queueList.add(new LinkedBlockingQueue<>());
            }
            // 1.2 each worker thread is responsible for listening to a queue
            for (int i = 0; i < 3; i++) {
                new Worker(queueList.get(i)).start();
            }

            System.out.println("NIOServer is starting, begin to listen port：" + 9000);
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
                    handleRequest(key);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void handleRequest(SelectionKey key) throws IOException, ClosedChannelException {
        SocketChannel channel = null;

        try {
            if (key.isAcceptable()) {
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                channel = serverSocketChannel.accept();
                if (channel != null) {
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                }
            } else if (key.isReadable()) {
                channel = (SocketChannel) key.channel();
                // get remote addr from channel
                String remoteAddr = channel.getRemoteAddress().toString();

                // assign requests to different queues through a hash algorithm
                int queueIndex = remoteAddr.hashCode() % queueList.size();
                queueList.get(queueIndex).put(key);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            if (channel != null) {
                channel.close();
            }
        }
    }

    class CachedImage {

        String filename;
        /**
         * full length
         */
        long imageLength;
        /**
         * already read length
         */
        long hasReadImageLength;

        public CachedImage(String filename, long imageLength, long hasReadImageLength) {
            this.filename = filename;
            this.imageLength = imageLength;
            this.hasReadImageLength = hasReadImageLength;
        }


        @Override
        public String toString() {
            return "CachedImage{" +
                    "filename='" + filename + '\'' +
                    ", imageLength=" + imageLength +
                    ", hasReadImageLength=" + hasReadImageLength +
                    '}';
        }
    }


    class Worker extends Thread {

        private LinkedBlockingQueue<SelectionKey> queue;

        public Worker(LinkedBlockingQueue<SelectionKey> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                SocketChannel channel = null;

                try {
                    SelectionKey key = queue.take();

                    channel = (SocketChannel) key.channel();
                    if (!channel.isOpen()) {
                        channel.close();
                        continue;
                    }
                    String remoteAddr = channel.getRemoteAddress().toString();

                    ByteBuffer buffer = ByteBuffer.allocate(10 * 1024);
                    int len = -1;

                    String filename = null;
                    if (cachedImageMap.containsKey(remoteAddr)) {
                        // worker thread support unpacking, so we should try to get filename from cache
                        filename = cachedImageMap.get(remoteAddr).filename;
                    } else {
                        filename = "E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\image\\tmp\\" + UUID.randomUUID() + ".jpg";
                    }

                    long imageLength = 0;

                    if (cachedImageMap.containsKey(remoteAddr)) {
                        imageLength = cachedImageMap.get(remoteAddr).imageLength;
                    } else {
                        len = channel.read(buffer);
                        buffer.flip();

                        if (len > 8) {
                            byte[] imageLengthBytes = new byte[8];
                            buffer.get(imageLengthBytes, 0, 8);

                            ByteBuffer imageLengthBuffer = ByteBuffer.allocate(8);
                            imageLengthBuffer.put(imageLengthBytes);
                            imageLengthBuffer.flip();
                            // get image file full length
                            imageLength = imageLengthBuffer.getLong();
                        } else if (len <= 0) {
                            channel.close();
                            continue;
                        }
                    }

                    long hasReadImageLength = 0;
                    if (cachedImageMap.containsKey(remoteAddr)) {
                        // get the previously read length
                        hasReadImageLength = cachedImageMap.get(remoteAddr).hasReadImageLength;
                    }

                    FileOutputStream imageOut = new FileOutputStream(filename);
                    FileChannel imageChannel = imageOut.getChannel();
                    // set position
                    imageChannel.position(imageChannel.size());

                    // only update hasReadImageLength
                    if (!cachedImageMap.containsKey(remoteAddr)) {
                        // if this time is not the first read, then fileSize should read, and not write into imageChannel
                        hasReadImageLength += imageChannel.write(buffer);
                        buffer.clear();
                    }

                    // update hasReadImageLength and write image file into imageChannel
                    while ((len = channel.read(buffer)) > 0) {
                        hasReadImageLength += len;
                        buffer.flip();
                        imageChannel.write(buffer);
                        buffer.clear();
                    }

                    if (cachedImageMap.get(remoteAddr) != null) {
                        // if read empty pack, skip
                        if (hasReadImageLength == cachedImageMap.get(remoteAddr).hasReadImageLength) {
                            channel.close();
                            continue;
                        }
                    }

                    imageChannel.close();
                    imageOut.close();

                    // if all data is read
                    if (hasReadImageLength == imageLength) {
                        ByteBuffer outBuffer = ByteBuffer.wrap("SUCCESS".getBytes());
                        channel.write(outBuffer);
                        cachedImageMap.remove(remoteAddr);
                    } else {
                        // if only part of the data is read, then cache the data
                        CachedImage cachedImage = new CachedImage(filename, imageLength, hasReadImageLength);
                        cachedImageMap.put(remoteAddr, cachedImage);
                        key.interestOps(SelectionKey.OP_READ);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (channel != null) {
                        try {
                            channel.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }

    }
}
