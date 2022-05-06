package com.wuyiccc.hellodfs.namenode.server;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * double write buffer
 *
 * @author wuyiccc
 * @date 2022/5/3 18:14
 */
public class DoubleBuffer {

    /**
     * single edit log buffer max size 25k
     */
    public static final Integer EDIT_LOG_BUFFER_LIMIT = 25 * 1024;


    /**
     * thread write into edit log buffer
     */
    private EditLogBuffer currentBuffer = new EditLogBuffer();

    /**
     * a buffer that synchronizes data to disk
     */
    private EditLogBuffer syncBuffer = new EditLogBuffer();


    long startTxId = 1L;


    /**
     * write edit log into memory buffer
     */
    public void write(EditLog editLog) throws IOException {
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
    public void flush() throws IOException {
        this.syncBuffer.flush();
        this.syncBuffer.clear();
    }

    class EditLogBuffer {


        private long endTxId = 0L;

        /**
         * SIZE = 50K
         */
        ByteArrayOutputStream buffer;


        public EditLogBuffer() {
            this.buffer = new ByteArrayOutputStream(EDIT_LOG_BUFFER_LIMIT * 2);
        }

        /**
         * write editLog to buffer
         *
         * @param editLog
         */
        public void write(EditLog editLog) throws IOException {
            endTxId = editLog.getTxId();
            buffer.write(editLog.getContent().getBytes());
            buffer.write("\n".getBytes());
            System.out.println("write a editslog: " + editLog.getContent() + ", current buffer size is : " + this.size());
        }

        /**
         * get current buffer write size
         *
         * @return
         */
        public Integer size() {
            return buffer.size();
        }

        public void flush() throws IOException {

            System.out.println("begin flush disk");
            String editLogFilePath = "E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\editslog\\edits-" + (startTxId) + "-" + endTxId + ".log";


            RandomAccessFile file = null;
            FileOutputStream out = null;
            FileChannel editsLogFileChannel = null;

            try {
                file = new RandomAccessFile(editLogFilePath, "rw");
                out = new FileOutputStream(file.getFD());
                editsLogFileChannel = out.getChannel();


                byte[] data = buffer.toByteArray();
                ByteBuffer dataBuffer = ByteBuffer.wrap(data);
                editsLogFileChannel.write(dataBuffer);
                // force flush data from os cache into disk
                editsLogFileChannel.force(false);
            } finally {
                if (out != null) {
                    out.close();
                }
                if (file != null) {
                    file.close();
                }
                if (editsLogFileChannel != null) {
                    editsLogFileChannel.close();
                }
            }

            startTxId = endTxId + 1;
        }

        /**
         * clear buffer cache data
         */
        public void clear() {
            buffer.reset();
        }
    }


}
