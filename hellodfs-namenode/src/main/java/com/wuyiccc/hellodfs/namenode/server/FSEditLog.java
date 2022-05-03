package com.wuyiccc.hellodfs.namenode.server;

import java.util.LinkedList;

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
    private long txIdSeq = 0;

    private DoubleBuffer editLogBuffer = new DoubleBuffer();

    /**
     * mark the sync data into disk status
     */
    private volatile Boolean isSyncRunning = false;



    private volatile Boolean isWaitSync = false;

    /**
     * sync to the disk max txId
     */
    private volatile Long syncMaxTxId = 0L;

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
            // get the global unique increasing txId, represents the sequence number of edit log
            this.txIdSeq++;
            long txId = txIdSeq;
            this.myTransactionId.set(txId);

            EditLog editLog = new EditLog(txId, content);

            editLogBuffer.write(editLog);

        }

        logSync();
    }

    private void logSync() {
        synchronized (this) {
            // if a thread flush memory data into disk
            if (isSyncRunning) {
                long txId = myTransactionId.get();

                // if a thread want flush buffer, but txId < syncMaxId, the thread should return (another thread is flushing this data into disk)
                if (txId <= syncMaxTxId) {
                    return;
                }

                // if a thread is wait sync, current thread return
                if (isWaitSync) {
                    return;
                }

                // mark there is a thread is waiting
                isWaitSync = true;
                while (isSyncRunning) {
                    try {
                        wait(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                isWaitSync = false;

            }
            // swap buffer
            editLogBuffer.setReadyToSync();
            this.syncMaxTxId = editLogBuffer.getSyncMaxTxId();

            this.isSyncRunning = true;

        }

        // begin flush memory data into disk
        editLogBuffer.flush();

        // reset mark bit
        synchronized (this) {
            isSyncRunning = false;
            // notify other thread which is waiting sync
            notifyAll();
        }
    }

    /**
     * a edit log object
     */
    class EditLog {

        long txId;

        String content;

        public EditLog(long txId, String content) {
            this.txId = txId;
            this.content = content;
        }
    }

    /**
     * memory double buffer
     */
    class DoubleBuffer {

        /**
         * thread write into edit log buffer
         */
        LinkedList<EditLog> currentBuffer = new LinkedList<>();

        /**
         * a buffer that synchronizes data to disk
         */
        LinkedList<EditLog> syncBuffer = new LinkedList<>();

        /**
         * write edit log into memory buffer
         *
         * @param editLog
         */
        public void write(EditLog editLog) {
            currentBuffer.add(editLog);
        }

        /**
         * swap two buffers in preparation for synchronizing data to disk
         */
        public void setReadyToSync() {
            LinkedList<EditLog> tmp = currentBuffer;
            currentBuffer = syncBuffer;
            syncBuffer = tmp;
        }

        /**
         * 获取sync buffer缓冲区里的最大一个txId
         *
         * @return
         */
        public Long getSyncMaxTxId() {
            return syncBuffer.getLast().txId;
        }

        /**
         * flush syncBuffer data into disk
         */
        public void flush() {
            for (EditLog editLog : syncBuffer) {
                System.out.println("write edit log into disk");
            }
            syncBuffer.clear();
        }
    }
}
