package com.wuyiccc.hellodfs.namenode.server;

/**
 *
 * The core components responsible for managing metadata
 *
 * @author wuyiccc
 * @date 2022/4/26 22:11
 */
public class FSNameSystem {


    private FSDirectory fsDirectory;

    private FSEditLog fsEditLog;


    /**
     * last checkpoint max TxId;
     */
    private long checkpointTxId;

    public FSNameSystem() {
        this.fsDirectory = new FSDirectory();
        this.fsEditLog = new FSEditLog();
    }

    public long getCheckpointTxId() {
        return checkpointTxId;
    }

    public void setCheckpointTxId(long checkpointTxId) {
        System.out.println("receive checkpoint txId:" + checkpointTxId);
        this.checkpointTxId = checkpointTxId;
    }

    /**
     * create a directory
     * @param path the directory path
     * @return create success or fail
     */
    public Boolean mkdir(String path) throws Exception {
        this.fsDirectory.mkdir(path);
        this.fsEditLog.logEdit("{'OP':'MKDIR','PATH':'"+ path +"'}");
        return true;
    }

    public void flush() {
        this.fsEditLog.flush();
    }

    public FSEditLog getFsEditLog() {
        return fsEditLog;
    }


}
