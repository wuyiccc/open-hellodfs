package com.wuyiccc.hellodfs.namenode.server;

/**
 * double write buffer
 *
 * @author wuyiccc
 * @date 2022/5/3 18:14
 */
public class DoubleBuffer {

    /**
     * single edit log buffer max size 512k
     */
    public static final Long EDIT_LOG_BUFFER_LIMIT = 512 * 1024L;

    /**
     * thread write into edit log buffer
     */
    private EditLogBuffer currentBuffer = new EditLogBuffer();

    /**
     * a buffer that synchronizes data to disk
     */
    private EditLogBuffer syncBuffer = new EditLogBuffer();


    /**
     * write edit log into memory buffer
     */
    public void write(EditLog editLog) {
        currentBuffer.write(editLog);
    }


    /**
     * check if it should write to disk
     *
     * @return
     */
    public boolean shouldSyncToDisk() {
        if (currentBuffer.size() >= EDIT_LOG_BUFFER_LIMIT) {
            return true;
        }
        return false;
    }

    /**
     * swap two buffers in preparation for synchronizing data to disk
     */
    public void setReadyToSync() {
        EditLogBuffer tmp = currentBuffer;
        currentBuffer = syncBuffer;
        syncBuffer = tmp;
    }

    /**
     * flush syncBuffer data into disk
     */
    public void flush() {
        this.syncBuffer.flush();
        this.syncBuffer.clear();
    }

    class EditLogBuffer {


        /**
         * write editLog to buffer
         *
         * @param editLog
         */
        public void write(EditLog editLog) {
        }

        /**
         * get current buffer write size
         *
         * @return
         */
        public Long size() {
            return 0L;
        }

        public void flush() {

        }

        public void clear() {

        }
    }


}
