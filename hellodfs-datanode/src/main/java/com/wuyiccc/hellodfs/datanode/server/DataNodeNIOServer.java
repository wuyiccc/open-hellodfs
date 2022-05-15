package com.wuyiccc.hellodfs.datanode.server;


import java.io.File;
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
            serverSocketChannel.socket().bind(new InetSocketAddress(DataNodeConfig.NIO_PORT), 100);
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

            System.out.println("NIOServer is starting, begin to listen port：" + DataNodeConfig.NIO_PORT);
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

                    System.out.println("receive client request: " + remoteAddr);

                    ByteBuffer buffer = ByteBuffer.allocate(10 * 1024);
                    // get filename from channel
                    String filename = getFilename(channel, buffer);
                    System.out.println("parse filename from channel: " + filename);
                    // if filename is null, skip read
                    if (filename == null) {
                        channel.close();
                        continue;
                    }

                    // get file length from channel
                    long imageLength = getImageLength(channel, buffer);
                    System.out.println("parse file size: " + imageLength);
                    // get already read file length from cache
                    long hasReadImageLength = getHasReadImageLength(channel);
                    System.out.println("hasReadImageLength: " + hasReadImageLength);


                    FileOutputStream imageOut = new FileOutputStream(filename);
                    FileChannel imageChannel = imageOut.getChannel();
                    imageChannel.position(imageChannel.size());

                    // write the remaining data in the buffer after the first read to the file
                    if (!cachedImageMap.containsKey(remoteAddr)) {
                        hasReadImageLength += imageChannel.write(buffer);
                        System.out.println("already write into disk file size: " + hasReadImageLength);
                        buffer.clear();
                    }

                    // loop read data from channel and write to the disk file
                    int len = -1;
                    while ((len = channel.read(buffer)) > 0) {
                        hasReadImageLength += len;
                        System.out.println("already write into disk file size: " + hasReadImageLength);
                        buffer.flip();
                        imageChannel.write(buffer);
                        buffer.clear();
                    }
                    imageChannel.close();
                    imageOut.close();

                    // if already read all file, the remove the cache, and return success to client
                    if (hasReadImageLength == imageLength) {
                        ByteBuffer outBuffer = ByteBuffer.wrap("SUCCESS".getBytes());
                        channel.write(outBuffer);
                        cachedImageMap.remove(remoteAddr);
                        System.out.println("file read completed, return to client success");
                    } else {
                        // cache the file
                        CachedImage cachedImage = new CachedImage(filename, imageLength, hasReadImageLength);
                        cachedImageMap.put(remoteAddr, cachedImage);
                        key.interestOps(SelectionKey.OP_READ);
                        System.out.println("file hasn't read completed, wait the next request, already cached file: " + cachedImage);
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

    private String getFilename(SocketChannel channel, ByteBuffer buffer) throws Exception {
        String filename = null;
        String remoteAddr = channel.getRemoteAddress().toString();

        // try to get filename from cache
        if (this.cachedImageMap.containsKey(remoteAddr)) {
            filename = this.cachedImageMap.get(remoteAddr).filename;
        } else {
            filename = getFilenameFromChannel(channel, buffer);
            if (filename == null) {
                return null;
            }
            // /image/product/iphone.jpg
            String[] filenameSplit = filename.split("/");

            String dirPath = DataNodeConfig.DATA_DIR;
            for (int i = 0; i < filenameSplit.length - 1; i++) {
                if (i == 0) {
                    continue;
                }
                dirPath += "\\" + filenameSplit[i];
            }

            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            filename = dirPath + "\\" + filenameSplit[filenameSplit.length - 1];
        }

        return filename;
    }

    /**
     * get filename from channel
     */
    private String getFilenameFromChannel(SocketChannel channel, ByteBuffer buffer) throws Exception {
        int len = channel.read(buffer);
        if (len > 0) {
            byte[] filenameLengthBytes = new byte[4];
            // get filename length from buffer, set into filenameLengthBytes
            // int(4 bytes)
            buffer.flip();
            buffer.get(filenameLengthBytes, 0, 4);

            ByteBuffer filenameLengthBuffer = ByteBuffer.allocate(4);
            filenameLengthBuffer.put(filenameLengthBytes);
            filenameLengthBuffer.flip();
            // convert byte data to int
            int filenameLength = filenameLengthBuffer.getInt();

            byte[] filenameBytes = new byte[filenameLength];
            // get filename from buffer, and set into filenameBytes
            buffer.get(filenameBytes, 0, filenameLength);
            // convert byte data to string
            String filename = new String(filenameBytes);
            return filename;
        }
        return null;
    }

    private Long getImageLength(SocketChannel channel, ByteBuffer buffer) throws Exception {
        Long imageLength = 0L;
        String remoteAddr = channel.getRemoteAddress().toString();

        if (this.cachedImageMap.containsKey(remoteAddr)) {
            imageLength = this.cachedImageMap.get(remoteAddr).imageLength;
        } else {
            // long (8 bytes)
            byte[] imageLengthBytes = new byte[8];
            buffer.get(imageLengthBytes, 0, 8);

            ByteBuffer imageLengthBuffer = ByteBuffer.allocate(8);
            imageLengthBuffer.put(imageLengthBytes);
            imageLengthBuffer.flip();
            imageLength = imageLengthBuffer.getLong();
        }

        return imageLength;
    }

    private Long getHasReadImageLength(SocketChannel channel) throws Exception {
        long hasReadImageLength = 0;
        String remoteAddr = channel.getRemoteAddress().toString();
        if (this.cachedImageMap.containsKey(remoteAddr)) {
            hasReadImageLength = this.cachedImageMap.get(remoteAddr).hasReadImageLength;
        }
        return hasReadImageLength;
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
}
