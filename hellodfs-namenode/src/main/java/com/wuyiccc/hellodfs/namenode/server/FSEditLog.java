package com.wuyiccc.hellodfs.namenode.server;

import java.io.IOException;
import java.util.List;

/**
 * Responsible for managing the core components of editLog
 *
 * @author wuyiccc
 * @date 2022/4/26 22:12
 */
public class FSEditLog {


    /**
     * current txId sequence
     */
    private long txIdSeq = 0L;

    /**
     * memory double buffer
     */
    private DoubleBuffer doubleBuffer = new DoubleBuffer();

    /**
     * mark the sync data into disk status
     */
    private volatile Boolean isSyncRunning = false;


    /**
     * sync to the disk max txId
     */
    private volatile Long syncTxId = 0L;

    /**
     * whether is scheduling a sync to disk operator
     */
    private volatile Boolean isSchedulingSync = false;

    /**
     * thread local txId
     */
    private ThreadLocal<Long> myTransactionId = new ThreadLocal<>();

    /**
     * save edit log
     *
     * @param content
     */
    public void logEdit(String content) {

        synchronized (this) {

            waitSchedulingSync();
            // get the global unique increasing txId, represents the sequence number of edit log
            this.txIdSeq++;
            long txId = txIdSeq;
            this.myTransactionId.set(txId);

            EditLog editLog = new EditLog(txId, content);

            try {
                doubleBuffer.write(editLog);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // check buffer capacity
            if (!doubleBuffer.shouldSyncToDisk()) {
                return;
            }

            isSchedulingSync = true;
        }

        logSync();
    }

    private void logSync() {
        synchronized (this) {

            long txId = myTransactionId.get();

            // if a thread flush memory data into disk
            if (isSyncRunning) {

                // if a thread want flush buffer, but txId < syncMaxId, the thread should return (another thread is flushing this data into disk)
                if (txId <= syncTxId) {
                    return;
                }

                try {
                    while (isSyncRunning) {
                        wait(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // swap buffer
            this.doubleBuffer.setReadyToSync();
            this.syncTxId = txId;

            this.isSchedulingSync = false;
            // notify waitSchedulingSync thread
            notifyAll();
            this.isSyncRunning = true;
        }

        // begin flush memory data into disk
        try {
            doubleBuffer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // reset mark bit
        synchronized (this) {
            isSyncRunning = false;
            // notify other thread which is waiting sync
            notifyAll();
        }
    }

    /**
     * wait scheduling sync to disk operator
     */
    private void waitSchedulingSync() {
        try {
            while (isSchedulingSync) {
                wait(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void flush() {
        try {
            doubleBuffer.setReadyToSync();
            doubleBuffer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getFlushedTxIds() {
        return doubleBuffer.getFlushedTxIds();
    }

    public String[] getBufferedEditsLog() {
        synchronized (this) {
            return this.doubleBuffer.getBufferedEditsLog();
        }
    }


}
