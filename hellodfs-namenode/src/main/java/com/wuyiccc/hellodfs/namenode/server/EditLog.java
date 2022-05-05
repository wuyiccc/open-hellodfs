package com.wuyiccc.hellodfs.namenode.server;

/**
 * Edit Log
 *
 * @author wuyiccc
 * @date 2022/5/3 18:15
 */
public class EditLog {


    private long txId;

    private String content;

    public EditLog(long txId, String content) {
        this.txId = txId;
        this.content = content;
    }

    public long getTxId() {
        return txId;
    }

    public void setTxId(long txId) {
        this.txId = txId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "EditLog{" +
                "txId=" + txId +
                ", content='" + content + '\'' +
                '}';
    }
}
