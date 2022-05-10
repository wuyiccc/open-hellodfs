package com.wuyiccc.hellodfs.backupnode.server;

import com.sun.org.apache.xpath.internal.operations.Or;
import com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdResponse;

import javax.naming.NamingEnumeration;
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

    public static final Integer CHECKPOINT_INTERVAL = 1 * 60;

    private BackupNode backupNode;

    private FSNameSystem fsNameSystem;

    private NameNodeRpcClient nameNodeRpcClient;

    private String lastFSImageFilePath = "";

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
                TimeUnit.SECONDS.sleep(CHECKPOINT_INTERVAL);

                System.out.println("begin to execute checkpoint");
                // maybe you should doCheckPoint and then removeFile?
                doCheckPoint();
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
     * @param fsImage
     * @throws Exception
     */
    private void uploadFSImageFile(FSImage fsImage) throws Exception{
        FSImageUploader fsImageUploader = new FSImageUploader(fsImage);
        fsImageUploader.start();
    }


}
