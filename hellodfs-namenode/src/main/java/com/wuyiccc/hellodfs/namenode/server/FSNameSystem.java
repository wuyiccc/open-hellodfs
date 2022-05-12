package com.wuyiccc.hellodfs.namenode.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
        recoverNamespace();
    }

    public long getCheckpointTxId() {
        return checkpointTxId;
    }

    /**
     * flush checkpoint txId into disk
     */
    public void saveCheckpointTxId() {
        String path = "E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\editslog\\checkpoint-txId.meta";

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

    /**
     * recover metadata
     */
    public void recoverNamespace() {
        try {
            loadFSImage();
            loadCheckpointTxId();
            loadEditsLog();
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
            in = new FileInputStream("E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\editslog\\fsimage.meta");
            channel = in.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            int count = channel.read(buffer);
            buffer.flip();
            String fsImageJson = new String(buffer.array(), 0, count);

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

    /**
     * load editsLog
     */
    private void loadEditsLog() throws Exception {

        File dir = new File("E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\editslog\\");
        File[] fileList = dir.listFiles();
        for (File file : fileList) {
            if (file.getName().contains("edits")) {
                String[] splitName = file.getName().split("-");
                long startTxId = Long.parseLong(splitName[1]);
                long endTxId = Long.parseLong(splitName[2]);

                // load editslog which txId scope include checkpointTxId or more than txId
                if (endTxId > checkpointTxId) {
                    String currentEditsLogFilePath = "E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\editslog\\edits-" + (startTxId) + "-" + endTxId + ".log";
                    List<String> editsLog = Files.readAllLines(Paths.get(currentEditsLogFilePath), StandardCharsets.UTF_8);

                    for (String editLogJson : editsLog) {
                        JSONObject editLog = JSONObject.parseObject(editLogJson);
                        long txId = editLog.getLongValue("txId");

                        if (txId > checkpointTxId) {
                            // redo to memory
                            String op = editLog.getString("OP");

                            if ("MKDIR".equals(op)) {
                                String path = editLog.getString("PATH");
                                try {
                                    this.fsDirectory.mkdir(path);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void loadCheckpointTxId() throws Exception {
        FileInputStream in = null;
        FileChannel channel = null;

        try {
            in = new FileInputStream("E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\editslog\\checkpoint-txId.meta");
            channel = in.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            int count = channel.read(buffer);
            buffer.flip();
            long checkpointTxId = Long.parseLong(new String(buffer.array(), 0, count));
            this.checkpointTxId = checkpointTxId;
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
