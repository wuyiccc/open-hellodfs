package com.wuyiccc.hellodfs.datanode.server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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

    public static void main(String[] args) throws Exception {

        FileOutputStream imageOut = null;
        FileChannel imageChannel = null;

        byte[] file = new NIOClient().readFile("datanode01", 9301, "/image/product/lingyu.jpg");
        ByteBuffer fileBuffer = ByteBuffer.wrap(file);

        String absoluteFilename = FileUtils.getAbsoluteFilename("/image/product/lingyu.jpg");

        imageOut = new FileOutputStream(absoluteFilename);
        imageChannel = imageOut.getChannel();

        imageChannel.write(fileBuffer);
    }


}
