package com.wuyiccc.hellodfs.datanode.server;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author wuyiccc
 * @date 2022/5/28 10:29
 */
public class NetworkRequest {

    public static final Integer REQUEST_SEND_FILE = 1;
    public static final Integer REQUEST_READ_FILE = 2;

    private SelectionKey key;

    private SocketChannel channel;

    /**
     * cach request data
     */
    private CachedRequest cachedRequest = new CachedRequest();
    private ByteBuffer cachedRequestTypeBuffer;
    private ByteBuffer cachedFilenameLengthBuffer;
    private ByteBuffer cachedFilenameBuffer;
    private ByteBuffer cachedFileLengthBuffer;
    private ByteBuffer cachedFileBuffer;

    public SelectionKey getKey() {
        return key;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }


    public Boolean hasCompletedRead() {
        return cachedRequest.hasCompletedRead;
    }

    /**
     * read and parse a request from network connect
     */
    public void read() {
        try {
            Integer requestType = null;
            if (cachedRequest.requestType != null) {
                requestType = cachedRequest.requestType;
            } else {
                requestType = getRequestType(channel);
            }
            if (requestType == null) {
                return;
            }
            System.out.println("parse request type from current connect：" + requestType);

            if (REQUEST_SEND_FILE.equals(requestType)) {
                handleSendFileRequest(channel, key);
            } else if (REQUEST_READ_FILE.equals(requestType)) {
                handleReadFileRequest(channel, key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * get current request type
     */
    public Integer getRequestType(SocketChannel channel) throws Exception {
        Integer requestType = null;

        if (cachedRequest.requestType != null) {
            return cachedRequest.requestType;
        }

        ByteBuffer requestTypeBuffer = null;
        if (cachedRequestTypeBuffer != null) {
            requestTypeBuffer = cachedRequestTypeBuffer;
        } else {
            requestTypeBuffer = ByteBuffer.allocate(4);
        }

        channel.read(requestTypeBuffer);

        if (!requestTypeBuffer.hasRemaining()) {
            requestTypeBuffer.rewind();
            requestType = requestTypeBuffer.getInt();
            cachedRequest.requestType = requestType;
        } else {
            cachedRequestTypeBuffer = requestTypeBuffer;
        }

        return requestType;
    }



    private void handleSendFileRequest(SocketChannel channel, SelectionKey key) throws Exception {
        // get filename from channel
        Filename filename = getFilename(channel);
        System.out.println("parse filename from channel: " + filename);
        if (filename == null) {
            return;
        }

        // get file length from channel
        Long fileLength = getFileLength(channel);
        System.out.println("parse file size: " + fileLength);
        if (fileLength == null) {
            return;
        }

        ByteBuffer fileBuffer = null;
        if (cachedFileBuffer != null) {
            fileBuffer = cachedFileBuffer;
        } else {
            fileBuffer = ByteBuffer.allocate(Integer.parseInt(String.valueOf(fileLength)));
        }

        channel.read(fileBuffer);

        if (!fileBuffer.hasRemaining()) {
            fileBuffer.rewind();
            cachedRequest.file = fileBuffer;
            cachedRequest.hasCompletedRead = true;
            System.out.println("current file read completed.......");
        } else {
            cachedFileBuffer = fileBuffer;
            System.out.println("current file hasn't read completed, wait next read.......");
            return;
        }
    }


    private void handleReadFileRequest(SocketChannel channel, SelectionKey key) throws Exception {
        Filename filename = getFilename(channel);
        System.out.println("parse filename from request：" + filename);
        if (filename == null) {
            return;
        }
        cachedRequest.hasCompletedRead = true;
    }



    private Filename getFilename(SocketChannel channel) throws Exception {
        Filename filename = new Filename();

        if (cachedRequest.filename != null) {
            return cachedRequest.filename;
        } else {
            String relativeFilename = getRelativeFilename(channel);
            if (relativeFilename == null) {
                return null;
            }

            String absoluteFilename = getAbsoluteFilename(relativeFilename);
            // /image/product/iphone.jpg
            filename.relativeFilename = relativeFilename;
            filename.absoluteFilename = absoluteFilename;

            cachedRequest.filename = filename;
        }

        return filename;
    }

    /**
     * get filename from channel
     */
    private String getRelativeFilename(SocketChannel channel) throws Exception {
        Integer filenameLength = null;
        String filename = null;

        if (cachedRequest.filenameLength == null) {
            ByteBuffer filenameLengthBuffer = null;
            if (cachedFilenameLengthBuffer != null) {
                filenameLengthBuffer = cachedFilenameLengthBuffer;
            } else {
                filenameLengthBuffer = ByteBuffer.allocate(4);
            }

            channel.read(filenameLengthBuffer);

            if (!filenameLengthBuffer.hasRemaining()) {
                filenameLengthBuffer.rewind();
                filenameLength = filenameLengthBuffer.getInt();
                cachedRequest.filenameLength = filenameLength;
            } else {
                cachedFilenameLengthBuffer = filenameLengthBuffer;
                return null;
            }
        }

        ByteBuffer filenameBuffer = null;
        if (cachedFilenameBuffer != null) {
            filenameBuffer = cachedFilenameBuffer;
        } else {
            filenameBuffer = ByteBuffer.allocate(filenameLength);
        }

        channel.read(filenameBuffer);

        if (!filenameBuffer.hasRemaining()) {
            filenameBuffer.rewind();
            filename = new String(filenameBuffer.array());
        } else {
            cachedFilenameBuffer = filenameBuffer;
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

        if (cachedRequest.fileLength != null) {
            return cachedRequest.fileLength;
        } else {
            ByteBuffer fileLengthBuffer = null;
            if (cachedFileLengthBuffer != null) {
                fileLengthBuffer = cachedFileLengthBuffer;
            } else {
                // long (8 bytes)
                fileLengthBuffer = ByteBuffer.allocate(8);
            }

            channel.read(fileLengthBuffer);

            if (!fileLengthBuffer.hasRemaining()) {
                fileLengthBuffer.rewind();
                fileLength = fileLengthBuffer.getLong();
                cachedRequest.fileLength = fileLength;
            } else {
                cachedFileLengthBuffer = fileLengthBuffer;
            }
        }

        return fileLength;
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
        Integer filenameLength;
        /**
         * file full length
         */
        Long fileLength;
        ByteBuffer file;
        Boolean hasCompletedRead = false;

        @Override
        public String toString() {
            return "CachedRequest{" +
                    "requestType=" + requestType +
                    ", filename=" + filename +
                    ", filenameLength=" + filenameLength +
                    ", fileLength=" + fileLength +
                    ", file=" + file +
                    ", hasCompletedRead=" + hasCompletedRead +
                    '}';
        }
    }


}
