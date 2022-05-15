package com.wuyiccc.hellodfs.namenode.server;

import java.net.SocketAddress;

/**
 * desc datanode info
 *
 * @author wuyiccc
 * @date 2022/5/1 19:20
 */
public class DataNodeInfo implements Comparable<DataNodeInfo>{


    private String ip;

    private String hostname;

    private long lastHeartBeatTime;

    private long storedDataSize;

    public DataNodeInfo(String ip, String hostname) {
        this.ip = ip;
        this.hostname = hostname;
        this.storedDataSize = 0L;
    }

    public long getStoredDataSize() {
        return storedDataSize;
    }

    public void setStoredDataSize(long storedDataSize) {
        this.storedDataSize = storedDataSize;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public long getLastHeartBeatTime() {
        return lastHeartBeatTime;
    }

    public void setLastHeartBeatTime(long lastHeartBeatTime) {
        this.lastHeartBeatTime = lastHeartBeatTime;
    }

    public void addStoredDataSize(long storedDataSize) {
        this.storedDataSize += storedDataSize;
    }

    @Override
    public int compareTo(DataNodeInfo o) {
        if (this.storedDataSize - o.getStoredDataSize() > 0) {
            return 1;
        } else if (this.storedDataSize - o.getStoredDataSize() < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
