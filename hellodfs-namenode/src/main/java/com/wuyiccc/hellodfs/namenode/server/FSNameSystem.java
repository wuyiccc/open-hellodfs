package com.wuyiccc.hellodfs.namenode.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
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
    private long checkpointTxId = 0;

    public FSNameSystem() {
        this.fsDirectory = new FSDirectory();
        this.fsEditLog = new FSEditLog(this);
    }

    public long getCheckpointTxId() {
        return checkpointTxId;
    }

    /**
     * flush checkpoint txId into disk
     */
    public void saveCheckpointTxId() {
        String path = "E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\editslog\\checkpoint-txid.meta";

        RandomAccessFile raf = null;
        FileOutputStream out = null;
        FileChannel channel = null;

        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }

            raf = new RandomAccessFile(path, "rw");
            out = new FileOutputStream(raf.getFD());
            channel = out.getChannel();


            ByteBuffer dataBuffer = ByteBuffer.wrap(String.valueOf(checkpointTxId).getBytes());
            channel.write(dataBuffer);
            // force flush data from os cache into disk
            channel.force(false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (raf != null) {
                    raf.close();
                }
                if (channel != null) {
                    channel.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void setCheckpointTxId(long checkpointTxId) {
        System.out.println("receive checkpoint txId:" + checkpointTxId);
        this.checkpointTxId = checkpointTxId;
    }

    /**
     * create a directory
     *
     * @param path the directory path
     * @return create success or fail
     */
    public Boolean mkdir(String path) throws Exception {
        this.fsDirectory.mkdir(path);
        this.fsEditLog.logEdit("{'OP':'MKDIR','PATH':'" + path + "'}");
        return true;
    }

    public void flush() {
        this.fsEditLog.flush();
    }

    public FSEditLog getFsEditLog() {
        return fsEditLog;
    }


}
