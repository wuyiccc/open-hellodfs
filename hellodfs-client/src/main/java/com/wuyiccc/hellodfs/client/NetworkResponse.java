package com.wuyiccc.hellodfs.client;

import java.nio.ByteBuffer;

/**
 * @author wuyiccc
 * @date 2022/5/29 0:16
 */
public class NetworkResponse {

    public static final String RESPONSE_SUCCESS = "SUCCESS";

    private String requestId;
    private String hostname;
    private String ip;
    private ByteBuffer lengthBuffer;
    private ByteBuffer buffer;
    private Boolean error;
    private Boolean finished;

    public ByteBuffer getLengthBuffer() {
        return lengthBuffer;
    }

    public void setLengthBuffer(ByteBuffer lengthBuffer) {
        this.lengthBuffer = lengthBuffer;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }


    @Override
    public String toString() {
        return "NetworkResponse{" +
                "requestId='" + requestId + '\'' +
                ", hostname='" + hostname + '\'' +
                ", ip='" + ip + '\'' +
                ", lengthBuffer=" + lengthBuffer +
                ", buffer=" + buffer +
                ", error=" + error +
                ", finished=" + finished +
                '}';
    }
}
