package com.wuyiccc.hellodfs.namenode.server;

/**
 * @author wuyiccc
 * @date 2022/5/22 23:27
 */
public class ReplicateTask {

    private String filename;

    private Long fileLength;

    private DataNodeInfo sourceDataNodeInfo;

    private DataNodeInfo destDataNodeInfo;


    public ReplicateTask(String filename, Long fileLength, DataNodeInfo sourceDataNodeInfo, DataNodeInfo destDataNodeInfo) {
        this.filename = filename;
        this.fileLength = fileLength;
        this.sourceDataNodeInfo = sourceDataNodeInfo;
        this.destDataNodeInfo = destDataNodeInfo;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getFileLength() {
        return fileLength;
    }

    public void setFileLength(Long fileLength) {
        this.fileLength = fileLength;
    }

    public DataNodeInfo getSourceDataNodeInfo() {
        return sourceDataNodeInfo;
    }

    public void setSourceDataNodeInfo(DataNodeInfo sourceDataNodeInfo) {
        this.sourceDataNodeInfo = sourceDataNodeInfo;
    }

    public DataNodeInfo getDestDataNodeInfo() {
        return destDataNodeInfo;
    }

    public void setDestDataNodeInfo(DataNodeInfo destDataNodeInfo) {
        this.destDataNodeInfo = destDataNodeInfo;
    }
}
