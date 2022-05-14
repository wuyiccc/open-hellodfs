package com.wuyiccc.hellodfs.backupnode.server;

import java.io.File;
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

    public static final Integer CHECKPOINT_INTERVAL = 20;

    private BackupNode backupNode;

    private FSNameSystem fsNameSystem;

    private NameNodeRpcClient nameNodeRpcClient;

    private String lastFSImageFilePath = "";

    private long checkpointTime = System.currentTimeMillis();

    public FSImageCheckpointer(BackupNode backupNode, FSNameSystem fsNameSystem, NameNodeRpcClient nameNodeRpcClient) {
        this.backupNode = backupNode;
        this.fsNameSystem = fsNameSystem;
        this.nameNodeRpcClient = nameNodeRpcClient;
    }


    @Override
    public void run() {
        System.out.println("FSImageCheckpoint scheduled thread start");

        while (this.backupNode.isRunning()) {
            try {

                if (!this.fsNameSystem.isFinishedRecovered()) {
                    System.out.println("hasn't finished metadata recover, jump execute checkpoint");
                    TimeUnit.SECONDS.sleep(1);
                    continue;
                }

                if (this.lastFSImageFilePath.equals("")) {
                    this.lastFSImageFilePath = this.fsNameSystem.getCheckpointFile();
                }

                long now = System.currentTimeMillis();

                if ((now - checkpointTime) / 1000 > CHECKPOINT_INTERVAL) {

                    TimeUnit.SECONDS.sleep(CHECKPOINT_INTERVAL);
                    // if namenode is down, don't execute checkpoint
                    if (!this.nameNodeRpcClient.isNameNodeRunning()) {
                        System.out.println("nameNode cannot access, don't execute checkpoint");
                        continue;
                    }

                    System.out.println("begin to execute checkpoint");
                    // maybe you should doCheckPoint and then removeFile?
                    doCheckPoint();
                    System.out.println("complete checkpoint");
                } else {
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void doCheckPoint() throws Exception {
        FSImage fsImage = this.fsNameSystem.getFSImageJson();
        removeLastFSImageFile();
        writeFSImageFile(fsImage);
        uploadFSImageFile(fsImage);
        updateCheckpointTxId(fsImage);
        saveCheckpointInfo(fsImage);
    }


    private void saveCheckpointInfo(FSImage fsImage) {
        String path = "E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\backupnode\\checkpoint-info.meta";

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


            long now = System.currentTimeMillis();
            this.checkpointTime = now;
            long checkpointTxId = fsImage.getMaxTxId();
            ByteBuffer dataBuffer = ByteBuffer.wrap((now + "_" + checkpointTxId + "_" + this.lastFSImageFilePath).getBytes());
            channel.write(dataBuffer);
            // force flush data from os cache into disk
            channel.force(false);

            System.out.println("checkpoint info flushed into disk");
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

    private void updateCheckpointTxId(FSImage fsImage) {
        this.nameNodeRpcClient.updateCheckpointTxId(fsImage.getMaxTxId());
    }


    private void removeLastFSImageFile() {
        File file = new File(this.lastFSImageFilePath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * flush fsImage into disk
     */
    private void writeFSImageFile(FSImage fsImage) throws Exception {
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


    /**
     * upload fsImage File
     *
     * @param fsImage
     * @throws Exception
     */
    private void uploadFSImageFile(FSImage fsImage) throws Exception {
        FSImageUploader fsImageUploader = new FSImageUploader(fsImage);
        fsImageUploader.start();
    }


}
