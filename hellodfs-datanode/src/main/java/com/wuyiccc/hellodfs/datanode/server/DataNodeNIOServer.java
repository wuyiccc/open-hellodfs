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

    private NameNodeRpcClient nameNodeRpcClient;


    private Map<String, CachedRequest> cachedRequests = new ConcurrentHashMap<>();

    /**
     * cache the type of request that has not finished reading
     */
    private Map<String, ByteBuffer> requestTypesByClient = new ConcurrentHashMap<>();

    /**
     * cache filename length data
     */
    private Map<String, ByteBuffer> filenameLengthByClient = new ConcurrentHashMap<>();

    /**
     * cache filename data
     */
    private Map<String, ByteBuffer> filenameByClient = new ConcurrentHashMap<>();

    /**
     * cache file length data
     */
    private Map<String, ByteBuffer> fileLengthByClient = new ConcurrentHashMap<>();


    /**
     * cache unread files
     */
    private Map<String, ByteBuffer> fileByClient = new ConcurrentHashMap<>();

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
                    handleEvents(key);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void handleEvents(SelectionKey key) throws IOException {
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
                String client = channel.getRemoteAddress().toString();
                int queueIndex = client.hashCode() % this.queueList.size();
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

        if (this.cachedRequests.containsKey(remoteAddr)) {
            handleSendFileRequest(channel, key);
            return;
        }

        Integer requestType = getRequestType(channel);
        if (requestType == null) {
            return;
        }

        if (SEND_FILE.equals(requestType)) {
            handleSendFileRequest(channel, key);
        } else if (READ_FILE.equals(requestType)) {
            handleReadFileRequest(channel, key);
        }

    }

    private void handleSendFileRequest(SocketChannel channel, SelectionKey key) throws Exception {

        String client = channel.getRemoteAddress().toString();
        // get filename from channel
        Filename filename = getFilename(channel);
        System.out.println("parse filename from channel: " + filename);
        // if filename is null, skip read
        if (filename == null) {
            return;
        }

        // get file length from channel
        Long fileLength = getFileLength(channel);
        System.out.println("parse file size: " + fileLength);
        if (fileLength == null) {
            return;
        }

        // get already read file length from cache
        long hasReadImageLength = getHasReadFileLength(channel);
        System.out.println("hasReadImageLength: " + hasReadImageLength);

        FileOutputStream imageOut = null;
        FileChannel imageChannel = null;

        try {
            imageOut = new FileOutputStream(filename.absoluteFilename);
            imageChannel = imageOut.getChannel();

            // set position, add data after the last data
            imageChannel.position(imageChannel.size());

            ByteBuffer fileBuffer = null;

            if (fileByClient.containsKey(client)) {
                fileBuffer = fileByClient.get(client);
            } else {
                fileBuffer = ByteBuffer.allocate(Integer.parseInt(String.valueOf(fileLength)));
            }

            hasReadImageLength += channel.read(fileBuffer);


            if (!fileBuffer.hasRemaining()) {
                fileBuffer.rewind();
                imageChannel.write(fileBuffer);
                fileByClient.remove(client);

                // if already read all file, the remove the cache, and return success to client
                ByteBuffer outBuffer = ByteBuffer.wrap("SUCCESS".getBytes());
                channel.write(outBuffer);
                cachedRequests.remove(client);
                System.out.println("file read completed, return to client success");
                nameNodeRpcClient.informReplicaReceived(filename.relativeFilename);
                System.out.println("datanode begin informReplicaReceived...");
                // delete op_read
                key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
            } else {
                fileByClient.put(client, fileBuffer);
                getCachedRequest(client).hasReadFileLength = hasReadImageLength;
                return;
            }
        } finally {
            imageChannel.close();
            imageOut.close();
        }
    }

    private void handleReadFileRequest(SocketChannel channel, SelectionKey key) throws Exception {
        String client = channel.getRemoteAddress().toString();

        // parse filename from request
        Filename filename = getFilename(channel);
        System.out.println("parse filename from request：" + filename);
        if (filename == null) {
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
            System.out.println("file ready send to client: " + client);
            cachedRequests.remove(client);
            // delete read
            key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
        }
    }

    private Integer getRequestType(SocketChannel channel) throws Exception {
        Integer requestType = null;
        String client = channel.getRemoteAddress().toString();

        if (getCachedRequest(client).requestType != null) {
            return getCachedRequest(client).requestType;
        }

        ByteBuffer requestTypeBuffer = null;

        if (requestTypesByClient.containsKey(client)) {
            requestTypeBuffer = requestTypesByClient.get(client);
        } else {
            requestTypeBuffer = ByteBuffer.allocate(4);
        }

        channel.read(requestTypeBuffer);

        if (!requestTypeBuffer.hasRemaining()) {
            requestTypeBuffer.rewind();
            requestType = requestTypeBuffer.getInt();
            System.out.println("current request type: " + requestType);

            requestTypesByClient.remove(client);

            CachedRequest cachedRequest = getCachedRequest(client);
            cachedRequest.requestType = requestType;
        } else {
            requestTypesByClient.put(client, requestTypeBuffer);
        }
        return requestType;
    }

    private Filename getFilename(SocketChannel channel) throws Exception {
        Filename filename = new Filename();
        String client = channel.getRemoteAddress().toString();

        if (getCachedRequest(client).filename != null) {
            return getCachedRequest(client).filename;
        } else {
            String relativeFilename = getRelativeFilename(channel);
            if (relativeFilename == null) {
                return null;
            }
            // /image/product/iphone.jpg
            filename.relativeFilename = relativeFilename;

            String absoluteFilename = getAbsoluteFilename(relativeFilename);
            filename.absoluteFilename = absoluteFilename;

            CachedRequest cachedRequest = getCachedRequest(client);
            cachedRequest.filename = filename;
        }
        return filename;
    }

    /**
     * get filename from channel
     */
    private String getRelativeFilename(SocketChannel channel) throws Exception {
        String client = channel.getRemoteAddress().toString();

        Integer filenameLength = null;
        String filename = null;

        if (!filenameByClient.containsKey(client)) {
            ByteBuffer filenameLengthBuffer = null;
            if (filenameLengthByClient.containsKey(client)) {
                filenameLengthBuffer = fileLengthByClient.get(client);
            } else {
                filenameLengthBuffer = ByteBuffer.allocate(4);
            }

            channel.read(filenameLengthBuffer);

            if (!filenameLengthBuffer.hasRemaining()) {
                filenameLengthBuffer.rewind();
                filenameLength = filenameLengthBuffer.getInt();
                filenameLengthByClient.remove(client);
            } else {
                filenameLengthByClient.put(client, filenameLengthBuffer);
                return null;
            }
        }

        ByteBuffer filenameBuffer = null;

        if (filenameByClient.containsKey(client)) {
            filenameBuffer = filenameByClient.get(client);
        } else {
            filenameBuffer = ByteBuffer.allocate(filenameLength);
        }

        channel.read(filenameBuffer);

        if (!filenameBuffer.hasRemaining()) {
            filenameBuffer.rewind();
            filename = new String(filenameBuffer.array());
            filenameByClient.remove(client);
        } else {
            filenameByClient.put(client, filenameBuffer);
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
            dir.mkdirs();
        }

        String absoluteFilename = dirPath + "\\" + relativeFilenameSplit[relativeFilenameSplit.length - 1];
        return absoluteFilename;
    }


    private Long getFileLength(SocketChannel channel) throws Exception {
        Long fileLength = null;
        String client = channel.getRemoteAddress().toString();

        if (getCachedRequest(client).fileLength != null) {
            return getCachedRequest(client).fileLength;
        } else {

            ByteBuffer fileLengthBuffer = null;

            if (fileLengthByClient.get(client) != null) {
                fileLengthBuffer = fileLengthByClient.get(client);
            } else {
                // long (8 bytes)
                fileLengthBuffer = ByteBuffer.allocate(8);
            }

            channel.read(fileLengthBuffer);

            if (!fileLengthBuffer.hasRemaining()) {
                fileLengthBuffer.rewind();
                fileLength = fileLengthBuffer.getLong();
                fileLengthByClient.remove(client);
                getCachedRequest(client).fileLength = fileLength;
            } else {
                fileLengthByClient.put(client, fileLengthBuffer);
            }
        }
        return fileLength;
    }

    private Long getHasReadFileLength(SocketChannel channel) throws Exception {
        String client = channel.getRemoteAddress().toString();
        if (getCachedRequest(client).hasReadFileLength != null) {
            return getCachedRequest(client).hasReadFileLength;
        }
        return 0L;
    }


    private CachedRequest getCachedRequest(String client) {
        CachedRequest cachedRequest = this.cachedRequests.get(client);

        if (cachedRequest == null) {
            cachedRequest = new CachedRequest();
            this.cachedRequests.put(client, cachedRequest);
        }
        return cachedRequest;
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

    class CachedRequest {


        Integer requestType;

        Filename filename;

        /**
         * file full length
         */
        Long fileLength;

        /**
         * already read length
         */
        Long hasReadFileLength;


        @Override
        public String toString() {
            return "CachedRequest{" +
                    "requestType=" + requestType +
                    ", filename=" + filename +
                    ", fileLength=" + fileLength +
                    ", hasReadFileLength=" + hasReadFileLength +
                    '}';
        }
    }
}
