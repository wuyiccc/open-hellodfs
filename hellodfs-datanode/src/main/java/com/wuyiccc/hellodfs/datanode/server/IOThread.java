package com.wuyiccc.hellodfs.datanode.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

/**
 * @author wuyiccc
 * @date 2022/5/28 14:19
 */
public class IOThread extends Thread {
    public static final Integer REQUEST_SEND_FILE = 1;
    public static final Integer REQUEST_READ_FILE = 2;

    private NetworkRequestQueue requestQueue = NetworkRequestQueue.getInstance();
    private NameNodeRpcClient nameNode;

    public IOThread(NameNodeRpcClient nameNode) {
        this.nameNode = nameNode;
    }

    @Override
    public void run() {
        while (true) {
            try {
                NetworkRequest request = requestQueue.poll();
                if (request == null) {
                    TimeUnit.MILLISECONDS.sleep(100);
                    continue;
                }

                Integer requestType = request.getRequestType();

                if (requestType.equals(REQUEST_SEND_FILE)) {
                    writeFileToLocalDisk(request);
                } else if (requestType.equals(REQUEST_READ_FILE)) {
                    readFileFromLocalDisk(request);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void writeFileToLocalDisk(NetworkRequest request) throws Exception {

        FileOutputStream localFileOut = null;
        FileChannel localFileChannel = null;

        try {
            localFileOut = new FileOutputStream(request.getAbsoluteFilename());
            localFileChannel = localFileOut.getChannel();
            // set position, add data after the last data
            localFileChannel.position(localFileChannel.size());

            int written = localFileChannel.write(request.getFile());

            // /image/product/iphone.jpg
            nameNode.informReplicaReceived(request.getRelativeFilename() + "_" + request.getFileLength());
            System.out.println("datanode begin informReplicaReceived...");

            NetworkResponse response = new NetworkResponse();
            response.setClient(request.getClient());
            response.setBuffer(ByteBuffer.wrap("SUCCESS".getBytes()));

            NetworkResponseQueues responseQueues = NetworkResponseQueues.getInstance();
            responseQueues.offer(request.getProcessorId(), response);
        } finally {
            if (localFileChannel != null) {
                localFileChannel.close();
            }
            if (localFileOut != null) {
                localFileOut.close();
            }
        }
    }

    private void readFileFromLocalDisk(NetworkRequest request) throws Exception {
        FileInputStream localFileIn = null;
        FileChannel localFileChannel = null;

        try {
            File file = new File(request.getAbsoluteFilename());
            Long fileLength = file.length();

            localFileIn = new FileInputStream(request.getAbsoluteFilename());
            localFileChannel = localFileIn.getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(8 + Integer.parseInt(String.valueOf(fileLength)));
            buffer.putLong(fileLength);
            int hasReadImageLength = localFileChannel.read(buffer);
            System.out.println("already read" + hasReadImageLength + " bytes data from local disk");

            buffer.rewind();

            NetworkResponse response = new NetworkResponse();
            response.setClient(request.getClient());
            response.setBuffer(buffer);

            NetworkResponseQueues responseQueues = NetworkResponseQueues.getInstance();
            responseQueues.offer(request.getProcessorId(), response);
        } finally {
            if (localFileChannel != null) {
                localFileChannel.close();
            }
            if (localFileIn != null) {
                localFileIn.close();
            }
        }
    }


}
