package com.wuyiccc.hellodfs.client;

import java.nio.ByteBuffer;

/**
 * @author wuyiccc
 * @date 2022/5/28 20:26
 */
public class NetworkRequest {

    private String id;

    private String hostname;

    private Integer nioPort;

    private ByteBuffer buffer;

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
}
