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

    private int nioPort;


    public DataNodeInfo(String ip, String hostname, int nioPort) {
        this.ip = ip;
        this.hostname = hostname;
        this.nioPort = nioPort;
        this.lastHeartBeatTime = System.currentTimeMillis();
        this.storedDataSize = 0L;
    }

    public int getNioPort() {
        return nioPort;
    }

    public void setNioPort(int nioPort) {
        this.nioPort = nioPort;
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

    @Override
    public String toString() {
        return "DataNodeInfo{" +
                "ip='" + ip + '\'' +
                ", hostname='" + hostname + '\'' +
                ", lastHeartBeatTime=" + lastHeartBeatTime +
                ", storedDataSize=" + storedDataSize +
                ", nioPort=" + nioPort +
                '}';
    }
}
