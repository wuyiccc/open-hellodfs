package com.wuyiccc.hellodfs.backupnode.server;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

/**
 * @author wuyiccc
 * @date 2022/5/9 21:41
 */
public class FSImageCheckpointer extends Thread {


    //public static final Integer CHECKPOINT_INTERVAL = 1 * 60 * 60;

    public static final Integer CHECKPOINT_INTERVAL = 1 * 60;

    private BackupNode backupNode;

    private FSNameSystem fsNameSystem;

    private String lastFSImageFilePath = "";

    public FSImageCheckpointer(BackupNode backupNode, FSNameSystem fsNameSystem) {
        this.backupNode = backupNode;
        this.fsNameSystem = fsNameSystem;
    }


    @Override
    public void run() {
        System.out.println("FSImageCheckpoint scheduled thread start");

        while (this.backupNode.isRunning()) {
            try {
                TimeUnit.SECONDS.sleep(CHECKPOINT_INTERVAL);


                System.out.println("begin to execute checkpoint");
                FSImage fsImage = this.fsNameSystem.getFSImageJson();
                // maybe you should doCheckPoint and then removeFile?
                removeLastFSImageFile();
                doCheckPoint(fsImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * flush fsImage into disk
     */
    private void doCheckPoint(FSImage fsImage) throws Exception {


        ByteBuffer buffer = ByteBuffer.wrap(fsImage.getFsImageJson().getBytes());

        String fsImageFilePath = "E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\backupnode\\fsimage-" + fsImage.getMaxTxId() + ".meta";

        // maybe write into disk fail? and lastFSImageFilePath shouldn't change?
        this.lastFSImageFilePath = fsImageFilePath;

        RandomAccessFile file = null;
        FileOutputStream out = null;
        FileChannel fsImageFileChannel = null;

        try {
            file = new RandomAccessFile(fsImageFilePath, "rw");
            out = new FileOutputStream(file.getFD());
            fsImageFileChannel = out.getChannel();

            fsImageFileChannel.write(buffer);
            // force flush data from os cache into disk
            fsImageFileChannel.force(false);
        } finally {
            if (out != null) {
                out.close();
            }
            if (file != null) {
                file.close();
            }
            if (fsImageFileChannel != null) {
                fsImageFileChannel.close();
            }
        }
    }


    private void removeLastFSImageFile() {
        File file = new File(this.lastFSImageFilePath);
        if (file.exists()) {
            file.delete();
        }
    }


}
