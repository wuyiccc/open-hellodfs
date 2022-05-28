package com.wuyiccc.hellodfs.datanode.server;

import java.nio.ByteBuffer;

/**
 * @author wuyiccc
 * @date 2022/5/28 14:47
 */
public class NetworkResponse {

    private ByteBuffer buffer;

    private String client;

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }
}
