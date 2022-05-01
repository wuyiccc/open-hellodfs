package com.wuyiccc.hellodfs.namenode.server;

/**
 * desc datanode info
 *
 * @author wuyiccc
 * @date 2022/5/1 19:20
 */
public class DataNodeInfo {


    private String ip;

    private String hostname;

    private long lastHeartBeatTime;

    public DataNodeInfo(String ip, String hostname) {
        this.ip = ip;
        this.hostname = hostname;
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
}