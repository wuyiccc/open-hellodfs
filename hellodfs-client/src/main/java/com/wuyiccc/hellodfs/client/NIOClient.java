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

    public Boolean sendFile(FileInfo fileInfo, Host host, ResponseCallback callback) {

        if (!networkManager.maybeConnect(host.getHostname(), host.getNioPort())) {
            return false;
        }

        NetworkRequest request = createSendFileRequest(fileInfo, host, callback);
        networkManager.sendRequest(request);

        return true;
    }

    private NetworkRequest createSendFileRequest(FileInfo fileInfo, Host host, ResponseCallback callback) {
        NetworkRequest request = new NetworkRequest();

        // 4 bytes(type data) + 4 bytes(filename length data) + some bytes(filename data) + 8 bytes(file length data) + some bytes(file data)
        ByteBuffer buffer = ByteBuffer.allocate(
                NetworkRequest.REQUEST_TYPE
                        + NetworkRequest.FILENAME_LENGTH
                        + fileInfo.getFilename().getBytes().length
                        + NetworkRequest.FILE_LENGTH
                        + (int) fileInfo.getFileLength());
        buffer.putInt(NetworkRequest.REQUEST_SEND_FILE);
        buffer.putInt(fileInfo.getFilename().getBytes().length);
        // set filename
        buffer.put(fileInfo.getFilename().getBytes());
        // set fileSize in transport stream header, the long type need 8 bytes
        buffer.putLong(fileInfo.getFileLength());
        buffer.put(fileInfo.getFile());
        buffer.rewind();

        request.setId(UUID.randomUUID().toString());
        request.setHostname(host.getHostname());
        request.setIp(host.getIp());
        request.setNioPort(host.getNioPort());
        request.setBuffer(buffer);
        request.setNeedResponse(false);
        request.setCallback(callback);

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
