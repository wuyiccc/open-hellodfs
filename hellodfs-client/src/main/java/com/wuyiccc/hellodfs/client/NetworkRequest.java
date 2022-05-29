package com.wuyiccc.hellodfs.client;

import java.nio.ByteBuffer;

/**
 * @author wuyiccc
 * @date 2022/5/28 20:26
 */
public class NetworkRequest {


    /**
     * store requestType need 4 bytes (int)
     */
    public static final Integer REQUEST_TYPE = 4;

    /**
     * store filename length need 4 bytes (int)
     */
    public static final Integer FILENAME_LENGTH = 4;

    /**
     * store file length need 8 bytes (long)
     */
    public static final Integer FILE_LENGTH = 8;

    public static final Integer REQUEST_SEND_FILE = 1;

    public static final Integer REQUEST_READ_FILE = 2;

    private Integer requestType;

    private String id;

    private String hostname;

    private Integer nioPort;

    private ByteBuffer buffer;

    private Boolean needResponse;

    private long sendTime;

    public Integer getRequestType() {
        return requestType;
    }
    public void setRequestType(Integer requestType) {
        this.requestType = requestType;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getHostname() {
        return hostname;
    }
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
    public Integer getNioPort() {
        return nioPort;
    }
    public void setNioPort(Integer nioPort) {
        this.nioPort = nioPort;
    }
    public ByteBuffer getBuffer() {
        return buffer;
    }
    public void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }
    public Boolean needResponse() {
        return needResponse;
    }
    public void setNeedResponse(Boolean needResponse) {
        this.needResponse = needResponse;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }
}
