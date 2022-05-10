package com.wuyiccc.hellodfs.namenode.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * @author wuyiccc
 * @date 2022/5/10 20:38
 */
public class FSImageUploadServer extends Thread {

    private Selector selector;

    public FSImageUploadServer() {
        this.init();
    }

    private void init() {
        ServerSocketChannel serverSocketChannel = null;
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(9000), 100);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("FSImageUploadServer starting, listen port: 9000 ...");

        while (true) {
            try {
                selector.select();
                Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();

                while (keysIterator.hasNext()) {
                    SelectionKey key = keysIterator.next();
                    keysIterator.remove();
                    try {
                        handleRequest(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private void handleRequest(SelectionKey key)
            throws IOException {
        if (key.isAcceptable()) {
            handleConnectRequest(key);
        } else if (key.isReadable()) {
            handleReadableRequest(key);
        } else if (key.isWritable()) {
            handleWritableRequest(key);
        }
    }

    /**
     * connect request
     */
    private void handleConnectRequest(SelectionKey key) throws IOException {
        SocketChannel channel = null;

        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            channel = serverSocketChannel.accept();
            if (channel != null) {
                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_READ);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (channel != null) {
                channel.close();
            }
        }
    }

    /**
     * read fSImage request
     */
    private void handleReadableRequest(SelectionKey key) throws IOException {
        SocketChannel channel = null;

        try {
            String fsImageFilePath = "E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\editslog\\fsimage.meta";

            RandomAccessFile fsImageRAF = null;
            FileOutputStream fsImageOut = null;
            FileChannel fsImageFileChannel = null;

            try {
                channel = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);

                int total = 0;
                int count = -1;

                if((count = channel.read(buffer)) > 0){
                    File file = new File(fsImageFilePath);
                    if(file.exists()) {
                        file.delete();
                    }

                    fsImageRAF = new RandomAccessFile(fsImageFilePath, "rw");
                    fsImageOut = new FileOutputStream(fsImageRAF.getFD());
                    fsImageFileChannel = fsImageOut.getChannel();

                    total += count;

                    buffer.flip();
                    fsImageFileChannel.write(buffer);
                    buffer.clear();
                } else {
                    // if channel.read() <= 0, close channel
                    channel.close();
                }

                while((count = channel.read(buffer)) > 0){
                    total += count;
                    buffer.flip();
                    fsImageFileChannel.write(buffer);
                    buffer.clear();
                }

                if(total > 0) {
                    System.out.println("write fsimage to disk success......");
                    fsImageFileChannel.force(false);
                    channel.register(selector, SelectionKey.OP_WRITE);
                }
            } finally {
                if(fsImageOut != null) {
                    fsImageOut.close();
                }
                if(fsImageRAF != null) {
                    fsImageRAF.close();
                }
                if(fsImageFileChannel != null) {
                    fsImageFileChannel.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(channel != null) {
                channel.close();
            }
        }
    }

    /**
     * return backupnode
     */
    private void handleWritableRequest(SelectionKey key) throws IOException {
        SocketChannel channel = null;

        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            buffer.put("SUCCESS".getBytes());
            buffer.flip();

            channel = (SocketChannel) key.channel();
            channel.write(buffer);

            System.out.println("FSImage upload success, return data to backupnode...");

            channel.register(selector, SelectionKey.OP_READ);
        } catch (Exception e) {
            e.printStackTrace();
            if (channel != null) {
                channel.close();
            }
        }
    }

}
