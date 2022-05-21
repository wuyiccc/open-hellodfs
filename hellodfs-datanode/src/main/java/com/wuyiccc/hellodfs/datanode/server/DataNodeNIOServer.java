package com.wuyiccc.hellodfs.datanode.server;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author wuyiccc
 * @date 2022/5/15 11:31
 */
public class DataNodeNIOServer extends Thread {

    public static final Integer SEND_FILE = 1;

    public static final Integer READ_FILE = 2;
    public static final Integer NIO_BUFFER_SIZE = 10 * 1024;

    private Selector selector;
    private List<LinkedBlockingQueue<SelectionKey>> queueList = new ArrayList<>();
    private Map<String, CachedImage> cachedImageMap = new ConcurrentHashMap<>();

    private Map<String, String> waitReadingFilesMap = new ConcurrentHashMap<>();

    private NameNodeRpcClient nameNodeRpcClient;


    public DataNodeNIOServer(NameNodeRpcClient nameNodeRpcClient) {
        ServerSocketChannel serverSocketChannel = null;

        try {
            this.nameNodeRpcClient = nameNodeRpcClient;

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
                    handleEvent(key);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void handleEvent(SelectionKey key) throws IOException {
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
                String remoteAddr = channel.getRemoteAddress().toString();
                int queueIndex = remoteAddr.hashCode() % this.queueList.size();
                this.queueList.get(queueIndex).put(key);
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
                    handleRequest(channel, key);
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

    private void handleRequest(SocketChannel channel, SelectionKey key) throws Exception {
        String remoteAddr = channel.getRemoteAddress().toString();
        System.out.println("receive client request" + remoteAddr);

        if (this.cachedImageMap.containsKey(remoteAddr)) {
            handleSendFileRequest(channel, key);
        } else {
            Integer requestType = getRequestType(channel);
            if (SEND_FILE.equals(requestType)) {
                handleSendFileRequest(channel, key);
            } else if (READ_FILE.equals(requestType)) {
                handleReadFileRequest(channel, key);
            }
        }
    }

    private void handleSendFileRequest(SocketChannel channel, SelectionKey key) throws Exception {

        String remoteAddr = channel.getRemoteAddress().toString();
        // get filename from channel
        Filename filename = getFilename(channel);
        System.out.println("parse filename from channel: " + filename);
        // if filename is null, skip read
        if (filename == null) {
            channel.close();
            return;
        }

        // get file length from channel
        long imageLength = getImageLength(channel);
        System.out.println("parse file size: " + imageLength);
        // get already read file length from cache
        long hasReadImageLength = getHasReadImageLength(channel);
        System.out.println("hasReadImageLength: " + hasReadImageLength);


        FileOutputStream imageOut = new FileOutputStream(filename.absoluteFilename);
        FileChannel imageChannel = imageOut.getChannel();
        // set position, add data after the last data
        imageChannel.position(imageChannel.size());

        ByteBuffer buffer = ByteBuffer.allocate(10 * 1024);

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
            nameNodeRpcClient.informReplicaReceived(filename.relativeFilename);
            System.out.println("datanode begin informReplicaReceived...");
            // delete op_read
            key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
        } else {
            // cache the file
            CachedImage cachedImage = new CachedImage(filename, imageLength, hasReadImageLength);
            cachedImageMap.put(remoteAddr, cachedImage);
            System.out.println("file hasn't read completed, wait the next request, already cached file: " + cachedImage);
        }
    }

    private void handleReadFileRequest(SocketChannel channel, SelectionKey key) throws Exception {
        String remoteAddr = channel.getRemoteAddress().toString();

        // parse filename from request
        Filename filename = getFilename(channel);
        System.out.println("parse filename from request：" + filename);
        if (filename == null) {
            channel.close();
            return;
        }

        File file = new File(filename.absoluteFilename);
        Long fileLength = file.length();

        FileInputStream imageIn = new FileInputStream(filename.absoluteFilename);
        FileChannel imageChannel = imageIn.getChannel();


        // long (mark the file length) + file
        ByteBuffer buffer = ByteBuffer.allocate(8 + Integer.parseInt(String.valueOf(fileLength)));
        buffer.putLong(fileLength);

        int hasReadImageLength = imageChannel.read(buffer);

        buffer.rewind();
        channel.write(buffer);

        imageChannel.close();
        imageIn.close();

        if (hasReadImageLength == fileLength) {
            System.out.println("file ready send to client: " + remoteAddr);
            // delete read
            key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
        }
    }

    private Integer getRequestType(SocketChannel channel) throws Exception {
        ByteBuffer requestType = ByteBuffer.allocate(4);
        channel.read(requestType);

        if (!requestType.hasRemaining()) {
            // set position = 0, and set limit = 4
            requestType.rewind();
            return requestType.getInt();
        }
        return -1;
    }

    private Filename getFilename(SocketChannel channel) throws Exception {
        Filename filename = new Filename();
        String remoteAddr = channel.getRemoteAddress().toString();

        if (this.cachedImageMap.containsKey(remoteAddr)) {
            filename = this.cachedImageMap.get(remoteAddr).filename;
        } else {
            String relativeFilename = getRelativeFilename(channel);
            if (relativeFilename == null) {
                return null;
            }
            // /image/product/iphone.jpg
            filename.relativeFilename = relativeFilename;

            String absoluteFilename = getAbsoluteFilename(relativeFilename);
            filename.absoluteFilename = absoluteFilename;
        }

        return filename;
    }

    /**
     * get filename from channel
     */
    private String getRelativeFilename(SocketChannel channel) throws Exception {
        String filename = null;

        ByteBuffer filenameLengthBuffer = ByteBuffer.allocate(4);
        channel.read(filenameLengthBuffer);

        if (!filenameLengthBuffer.hasRemaining()) {
            filenameLengthBuffer.rewind();
            Integer filenameLength = filenameLengthBuffer.getInt();

            ByteBuffer filenameBuffer = ByteBuffer.allocate(filenameLength);
            channel.read(filenameBuffer);

            if (!filenameBuffer.hasRemaining()) {
                filenameBuffer.rewind();
                filename = new String(filenameBuffer.array());
            }
        }

        return filename;
    }

    private String getAbsoluteFilename(String relativeFilename) throws Exception {
        String[] relativeFilenameSplit = relativeFilename.split("/");

        String dirPath = DataNodeConfig.DATA_DIR;
        for (int i = 0; i < relativeFilenameSplit.length - 1; i++) {
            if (i == 0) {
                continue;
            }
            dirPath += "\\" + relativeFilenameSplit[i];
        }

        File dir = new File(dirPath);
        if (!dir.exists()) {
            return null;
        }

        String absoluteFilename = dirPath + "\\" + relativeFilenameSplit[relativeFilenameSplit.length - 1];
        return absoluteFilename;
    }


    private Long getImageLength(SocketChannel channel) throws Exception {
        Long imageLength = 0L;
        String remoteAddr = channel.getRemoteAddress().toString();

        if (this.cachedImageMap.containsKey(remoteAddr)) {
            imageLength = this.cachedImageMap.get(remoteAddr).imageLength;
        } else {
            // long (8 bytes)
            ByteBuffer imageLengthBuffer = ByteBuffer.allocate(8);
            channel.read(imageLengthBuffer);
            if (!imageLengthBuffer.hasRemaining()) {
                imageLength = imageLengthBuffer.getLong();
            }
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

    class Filename {

        String relativeFilename;

        String absoluteFilename;

        @Override
        public String toString() {
            return "Filename{" +
                    "relativeFilename='" + relativeFilename + '\'' +
                    ", absoluteFilename='" + absoluteFilename + '\'' +
                    '}';
        }
    }

    class CachedImage {

        Filename filename;

        /**
         * full length
         */
        long imageLength;

        /**
         * already read length
         */
        long hasReadImageLength;


        public CachedImage(Filename filename, long imageLength, long hasReadImageLength) {
            this.filename = filename;
            this.imageLength = imageLength;
            this.hasReadImageLength = hasReadImageLength;
        }

        @Override
        public String toString() {
            return "CachedImage{" +
                    "filename=" + filename +
                    ", imageLength=" + imageLength +
                    ", hasReadImageLength=" + hasReadImageLength +
                    '}';
        }
    }
}
