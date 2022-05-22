package com.wuyiccc.hellodfs.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * @author wuyiccc
 * @date 2022/5/15 11:34
 */
public class NIOClient {

    public static final Integer SEND_FILE = 1;

    public static final Integer READ_FILE = 2;

    public void sendFile(String hostname, int nioPort, byte[] file, String filename, long fileSize) {
        SocketChannel channel = null;
        Selector selector = null;
        ByteBuffer buffer = null;
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
                            while (!channel.finishConnect()) {
                                TimeUnit.MILLISECONDS.sleep(100);
                            }
                        }

                        System.out.println("finished connect with nioserver");

                        byte[] filenameBytes = filename.getBytes();

                        // 4 bytes(type data) + 4 bytes(filename length data) + some bytes(filename data) + 8 bytes(file length data) + some bytes(file data)
                        buffer = ByteBuffer.allocate(4 + 4 + filenameBytes.length + 8 + (int) fileSize);

                        buffer.putInt(SEND_FILE);
                        // set filename
                        buffer.putInt(filenameBytes.length);
                        buffer.put(filenameBytes);

                        // set fileSize in transport stream header, the long type need 8 bytes
                        buffer.putLong(fileSize);
                        buffer.put(file);
                        buffer.rewind();


                        // buffer: header(request type + filenameLength + filename + fileSize) + file
                        int sentData = channel.write(buffer);

                        System.out.println("already send: " + sentData + " bytes data");


                        if (buffer.hasRemaining()) {
                            System.out.println("If the packet is not sent this time, send it next time");
                            // continue to write
                            key.interestOps(SelectionKey.OP_WRITE);
                        } else {
                            System.out.println("The local data packet is sent and the server is ready to read the response");
                            key.interestOps(SelectionKey.OP_READ);
                        }

                    } else if (key.isWritable()) {
                        channel = (SocketChannel) key.channel();
                        channel.write(buffer);
                        System.out.println("last sent data packet has not send finished, now continue to send data in this time");
                        if (!buffer.hasRemaining()) {
                            key.interestOps(SelectionKey.OP_READ);
                        }
                    } else if (key.isReadable()) {
                        channel = (SocketChannel) key.channel();

                        buffer = ByteBuffer.allocate(1024);
                        int len = channel.read(buffer);

                        buffer.flip();

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


    public byte[] readFile(String hostname, int nioPort, String filename) {

        ByteBuffer fileLengthBuffer = null;
        Long fileLength = null;
        ByteBuffer fileBuffer = null;

        byte[] file = null;

        SocketChannel channel = null;
        Selector selector = null;

        try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(hostname, nioPort));

            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_CONNECT);

            boolean reading = true;

            while (reading) {
                selector.select();

                Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
                while (keysIterator.hasNext()) {
                    SelectionKey key = keysIterator.next();
                    keysIterator.remove();

                    if (key.isConnectable()) {
                        channel = (SocketChannel) key.channel();

                        if (channel.isConnectionPending()) {
                            // finished tcp connected
                            channel.finishConnect();
                        }

                        byte[] filenameBytes = filename.getBytes();

                        // int 4 bytes(oper type), int 4 bytes(filename length), filename.length
                        ByteBuffer readFileRequest = ByteBuffer.allocate(8 + filenameBytes.length);
                        // oper type data
                        readFileRequest.putInt(READ_FILE);
                        // filename length data
                        readFileRequest.putInt(filenameBytes.length);
                        // filename data
                        readFileRequest.put(filenameBytes);
                        readFileRequest.flip();

                        int sentData = channel.write(readFileRequest);
                        System.out.println("already send:" + sentData + " bytes data to" + hostname + "'s" + nioPort + "port");

                        channel.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        channel = (SocketChannel) key.channel();

                        if (fileLength == null) {
                            if (fileLengthBuffer == null) {
                                fileLengthBuffer = ByteBuffer.allocate(8);
                            }
                            channel.read(fileLengthBuffer);
                            if (!fileLengthBuffer.hasRemaining()) {
                                fileLengthBuffer.rewind();
                                fileLength = fileLengthBuffer.getLong();
                            }
                        }

                        if (fileLength != null) {
                            if (fileBuffer == null) {
                                fileBuffer = ByteBuffer.allocate(Integer.parseInt(String.valueOf(fileLength)));
                            }
                            channel.read(fileBuffer);
                            if (!fileBuffer.hasRemaining()) {
                                fileBuffer.rewind();
                                file = fileBuffer.array();
                                reading = false;
                            }
                        }

                    }
                }
            }
            return file;
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
        return null;
    }


}
