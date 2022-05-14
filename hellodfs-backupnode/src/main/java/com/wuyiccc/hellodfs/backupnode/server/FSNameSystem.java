package com.wuyiccc.hellodfs.backupnode.server;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileInputStream;
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

    private long checkpointTime;

    private long syncedTxId;

    private volatile boolean finishedRecovered = false;

    public FSNameSystem() {
        this.fsDirectory = new FSDirectory();
        recoverNamespace();
    }

    public boolean isFinishedRecovered() {
        return finishedRecovered;
    }

    public void setIsFinishedRecovered(boolean finishedRecovered) {
        this.finishedRecovered = finishedRecovered;
    }


    public long getCheckpointTime() {
        return checkpointTime;
    }

    public void setCheckpointTime(long checkpointTime) {
        this.checkpointTime = checkpointTime;
    }

    /**
     * create a directory
     *
     * @param path the directory path
     * @return create success or fail
     */
    public Boolean mkdir(long txId, String path) throws Exception {
        this.fsDirectory.mkdir(txId, path);
        return true;
    }


    public FSImage getFSImageJson() throws Exception {
        return this.fsDirectory.getFSImage();
    }

    /**
     * get synced maxTxId
     */
    public long getSyncedTxId() {
        return this.fsDirectory.getFSImage().getMaxTxId();
    }

    public void recoverNamespace() {
        try {
            loadCheckpointInfo();
            loadFSImage();
            this.finishedRecovered = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * load fsimage into memory
     */
    private void loadFSImage() throws Exception {
        FileInputStream in = null;
        FileChannel channel = null;

        try {

            String path = "E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\backupnode\\fsimage-" + syncedTxId + ".meta";
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("fsimage file cannot found, jump over");
                return;
            }
            in = new FileInputStream(path);
            channel = in.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            int count = channel.read(buffer);
            buffer.flip();
            String fsImageJson = new String(buffer.array(), 0, count);
            System.out.println("recover fsImage data into memory: " + fsImageJson);

            FSDirectory.INode rootDirTree = JSONObject.parseObject(fsImageJson, FSDirectory.INode.class);
            this.fsDirectory.setRootDirTree(rootDirTree);
        } finally {
            if (in != null) {
                in.close();
            }
            if (channel != null) {
                channel.close();
            }
        }
    }


    private void loadCheckpointInfo() throws Exception {
        FileInputStream in = null;
        FileChannel channel = null;

        try {
            String path = "E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\backupnode\\checkpoint-info.meta";
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("cannot found checkpoint-info.meta");
                return;
            }

            in = new FileInputStream(path);
            channel = in.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            int count = channel.read(buffer);
            buffer.flip();

            String checkpointInfo = new String(buffer.array(), 0, count);
            long checkpointTime = Long.parseLong(checkpointInfo.split("_")[0]);
            long syncedTxId = Long.parseLong(checkpointInfo.split("_")[1]);

            System.out.println("recover checkpoint time: " + checkpointTime + ", syncedTxId: " + syncedTxId);

            this.checkpointTime = checkpointTime;
            this.syncedTxId = syncedTxId;
            this.fsDirectory.setMaxTxId(syncedTxId);
        } finally {
            if (in != null) {
                in.close();
            }
            if (channel != null) {
                channel.close();
            }
        }
    }

}
