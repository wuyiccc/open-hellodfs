package com.wuyiccc.hellodfs.backupnode.server;

/**
 * @author wuyiccc
 * @date 2022/5/9 22:50
 */
public class FSImage {

    private long maxTxId;

    private String fsImageJson;

    public FSImage(long maxTxId, String fsImageJson) {
        this.maxTxId = maxTxId;
        this.fsImageJson = fsImageJson;
    }

    public long getMaxTxId() {
        return maxTxId;
    }

    public void setMaxTxId(long maxTxId) {
        this.maxTxId = maxTxId;
    }

    public String getFsImageJson() {
        return fsImageJson;
    }

    public void setFsImageJson(String fsImageJson) {
        this.fsImageJson = fsImageJson;
    }

    @Override
    public String toString() {
        return "FSImage{" +
                "maxTxId=" + maxTxId +
                ", fsImageJson='" + fsImageJson + '\'' +
                '}';
    }
}
