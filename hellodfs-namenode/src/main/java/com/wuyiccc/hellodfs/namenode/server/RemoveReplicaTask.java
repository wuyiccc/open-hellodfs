package com.wuyiccc.hellodfs.namenode.server;

/**
 * @author wuyiccc
 * @date 2022/5/23 23:18
 */
public class RemoveReplicaTask {

    private String filename;

    private DataNodeInfo dataNodeInfo;

    public RemoveReplicaTask(String filename, DataNodeInfo dataNodeInfo) {
        this.filename = filename;
        this.dataNodeInfo = dataNodeInfo;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public DataNodeInfo getDataNodeInfo() {
        return dataNodeInfo;
    }

    public void setDataNodeInfo(DataNodeInfo dataNodeInfo) {
        this.dataNodeInfo = dataNodeInfo;
    }
}
