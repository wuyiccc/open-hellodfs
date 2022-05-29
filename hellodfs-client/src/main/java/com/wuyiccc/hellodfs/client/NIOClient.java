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

    public byte[] readFile(Host host, String filename, Boolean retry) throws Exception {
        if (!networkManager.maybeConnect(host.getHostname(), host.getNioPort())) {
            if (retry) {
                throw new Exception();
            }
        }

        NetworkRequest request = createReadFileRequest(host, filename, null);
        networkManager.sendRequest(request);

        NetworkResponse response = networkManager.waitResponse(request.getId());

        if (response.getError()) {
            if (retry) {
                throw new Exception();
            }
        }

        return response.getBuffer().array();
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


    private NetworkRequest createReadFileRequest(Host host, String filename, ResponseCallback callback) {

        NetworkRequest request = new NetworkRequest();

        byte[] filenameBytes = filename.getBytes();

        // int 4 bytes(oper type), int 4 bytes(filename length), filename.length
        ByteBuffer buffer = ByteBuffer.allocate(NetworkRequest.REQUEST_TYPE + NetworkRequest.FILENAME_LENGTH + filenameBytes.length);

        // oper type data
        buffer.putInt(NetworkRequest.REQUEST_READ_FILE);
        // filename length data
        buffer.putInt(filenameBytes.length);
        // filename data
        buffer.put(filenameBytes);
        buffer.rewind();

        request.setId(UUID.randomUUID().toString());
        request.setHostname(host.getHostname());
        request.setIp(host.getIp());
        request.setNioPort(host.getNioPort());
        request.setBuffer(buffer);
        request.setNeedResponse(true);
        request.setCallback(callback);

        return request;
    }

}
