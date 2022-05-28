package com.wuyiccc.hellodfs.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.UUID;

/**
 * @author wuyiccc
 * @date 2022/5/15 11:34
 */
public class NIOClient {

    private NetworkManager networkManager;

    public NIOClient() {
        this.networkManager = new NetworkManager();
    }

    public Boolean sendFile(String hostname, int nioPort, byte[] file, String filename, long fileLength) {

        try {
            if (!networkManager.maybeConnect(hostname, nioPort)) {
                return false;
            }

            NetworkRequest request = createSendFileRequest(hostname, nioPort, file, filename, fileLength);
            networkManager.sendRequest(request);

            NetworkResponse response = networkManager.waitResponse(request.getId());
            ByteBuffer buffer = response.getBuffer();
            String responseStatus = new String(buffer.array(), 0, buffer.remaining());

            System.out.println("[" + Thread.currentThread().getName() + "]received" + hostname + "'s response：" + responseStatus);

            if (responseStatus.equals(NetworkResponse.RESPONSE_SUCCESS)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private NetworkRequest createSendFileRequest(String hostname, Integer nioPort, byte[] file, String filename, long fileLength) {

        NetworkRequest request = new NetworkRequest();

        // 4 bytes(type data) + 4 bytes(filename length data) + some bytes(filename data) + 8 bytes(file length data) + some bytes(file data)
        ByteBuffer buffer = ByteBuffer.allocate(NetworkRequest.REQUEST_TYPE + NetworkRequest.FILENAME_LENGTH + filename.getBytes().length + NetworkRequest.FILE_LENGTH + (int) fileLength);
        buffer.putInt(NetworkRequest.REQUEST_SEND_FILE);
        buffer.putInt(filename.getBytes().length);
        // set filename
        buffer.put(filename.getBytes());
        // set fileSize in transport stream header, the long type need 8 bytes
        buffer.putLong(fileLength);
        buffer.put(file);
        buffer.rewind();

        request.setId(UUID.randomUUID().toString());
        request.setHostname(hostname);
        request.setNioPort(nioPort);
        request.setBuffer(buffer);
        request.setNeedResponse(true);

        return request;
    }

    public byte[] readFile(String hostname, int nioPort, String filename) throws IOException {

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
                        readFileRequest.putInt(NetworkRequest.REQUEST_READ_FILE);
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
            throw e;
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
