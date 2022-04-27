package com.wuyiccc.hellodfs;

import java.util.LinkedList;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

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
     * save edit log
     *
     * @param content
     */
    public void logEdit(String content) {

        synchronized (this) {
            // get the global unique increasing txId, represents the sequence number of edit log
            txIdSeq++;
            long txId = txIdSeq;

            EditLog editLog = new EditLog(txId, content);

            editLogBuffer.write(editLog);

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
        List<EditLog> currentBuffer = new LinkedList<>();

        /**
         * a buffer that synchronizes data to disk
         */
        List<EditLog> syncBuffer = new LinkedList<>();

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
            List<EditLog> tmp = currentBuffer;
            currentBuffer = syncBuffer;
            syncBuffer = tmp;
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
