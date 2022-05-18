package com.wuyiccc.hellodfs.namenode.server;

import com.alibaba.fastjson.JSONObject;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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

    private Map<String, List<DataNodeInfo>> replicasByFilenameMap = new HashMap<>();

    private DataNodeManager dataNodeManager;

    public FSNameSystem(DataNodeManager dataNodeManager) {
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
        this.fsEditLog.logEdit(EditLogFactory.mkdir(path));
        return true;
    }

    /**
     * create file
     *
     * @param filename /products/img0001.jpg
     * @return
     * @throws Exception
     */
    public Boolean create(String filename) throws Exception {

        if (!this.fsDirectory.create(filename)) {
            return false;
        }
        this.fsEditLog.logEdit(EditLogFactory.create(filename));
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

            String path = "E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\editslog\\fsimage.meta";
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

    /**
     * load editsLog
     */
    private void loadEditsLog() throws Exception {

        File dir = new File("E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\editslog\\");

        List<File> fileList = new ArrayList<>();

        for (File file : dir.listFiles()) {
            fileList.add(file);
        }

        fileList.removeIf(file -> !file.getName().contains("edits"));


        Collections.sort(fileList, (o1, o2) -> {
            Integer o1StartTxId = Integer.parseInt(o1.getName().split("-")[1]);
            Integer o2StartTxId = Integer.parseInt(o2.getName().split("-")[1]);
            return o1StartTxId - o2StartTxId;
        });

        if (fileList == null || fileList.size() == 0) {
            System.out.println("cannot found editslog");
            return;
        }

        for (File file : fileList) {
            if (file.getName().contains("edits")) {
                String[] splitName = file.getName().split("-");
                long startTxId = Long.parseLong(splitName[1]);
                long endTxId = Long.parseLong(splitName[2].split("\\.")[0]);

                // load editslog which txId scope include checkpointTxId or more than txId
                if (endTxId > checkpointTxId) {


                    String currentEditsLogFilePath = "E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\editslog\\edits-" + (startTxId) + "-" + endTxId + ".log";
                    List<String> editsLog = Files.readAllLines(Paths.get(currentEditsLogFilePath), StandardCharsets.UTF_8);

                    for (String editLogJson : editsLog) {
                        JSONObject editLog = JSONObject.parseObject(editLogJson);
                        long txId = editLog.getLongValue("txId");

                        if (txId > checkpointTxId) {
                            System.out.println("begin to recover editslog: " + editLogJson);
                            // redo to memory
                            String op = editLog.getString("OP");

                            if ("MKDIR".equals(op)) {
                                String path = editLog.getString("PATH");
                                try {
                                    this.fsDirectory.mkdir(path);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if ("CREATE".equals(op)) {
                                String path = editLog.getString("PATH");
                                try {
                                    this.fsDirectory.create(path);
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
            String path = "E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\editslog\\checkpoint-txId.meta";
            File file = new File(path);
            if (!file.exists()) {
                System.out.println("cannot found checkpoint-txId.meta");
                return;
            }

            in = new FileInputStream(path);
            channel = in.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            int count = channel.read(buffer);
            buffer.flip();
            long checkpointTxId = Long.parseLong(new String(buffer.array(), 0, count));
            System.out.println("recover checkpoint txId: " + checkpointTxId);
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


    public void addReceivedReplica(String hostname, String ip, String filename) {
       synchronized (this.replicasByFilenameMap) {
           List<DataNodeInfo> replicas = replicasByFilenameMap.get(filename);
           if (replicas == null) {
               replicas = new ArrayList<>();
               replicasByFilenameMap.put(filename, replicas);
           }

           DataNodeInfo dataNodeInfo = this.dataNodeManager.getDataNodeInfo(ip, hostname);
           replicas.add(dataNodeInfo);

           System.out.println("received replica info, current replicasByFilenameMap: " + replicasByFilenameMap);
       }
    }
}
