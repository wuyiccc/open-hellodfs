package com.wuyiccc.hellodfs.datanode.server;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * datanode bootstrap class node
 *
 * @author wuyiccc
 * @date 2022/4/30 20:21
 */
public class DataNode {

    private volatile Boolean shouldRun;


    private NameNodeRpcClient nameNodeRpcClient;

    private DataNode() throws Exception {
        this.shouldRun = true;

        this.nameNodeRpcClient = new NameNodeRpcClient();
        this.nameNodeRpcClient.register();
        this.nameNodeRpcClient.startHeartBeat();

        StorageInfo storageInfo = getStorageInfo();
        if (storageInfo != null) {
            this.nameNodeRpcClient.reportAllStorageInfo(storageInfo);
        }

        DataNodeNIOServer nioServer = new DataNodeNIOServer(this.nameNodeRpcClient);
        nioServer.start();
    }

    private StorageInfo getStorageInfo() {
        StorageInfo storageInfo = new StorageInfo();

        File parentDataDir = new File(DataNodeConfig.DATA_DIR);
        File[] children = parentDataDir.listFiles();
        if (children == null || children.length == 0) {
            return null;
        }

        for (File child : children) {
            scanFiles(child, storageInfo);
        }
        return storageInfo;
    }

    private void scanFiles(File dir, StorageInfo storageInfo) {
        if (dir.isFile()) {
            String path = dir.getPath();
            path = path.substring(DataNodeConfig.DATA_DIR.length());
            path = path.replace("\\\\", "/");

            storageInfo.addFilename(path);
            storageInfo.addStoredDataSize(dir.length());
            return;
        }

        // is directory, recursive
        File[] children = dir.listFiles();
        if (children == null || children.length == 0) {
            return;
        }

        for (File child : children) {
            scanFiles(child, storageInfo);
        }
    }

    private void start() {
        try {
            while (shouldRun) {
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws Exception {
        DataNode dataNode = new DataNode();
        dataNode.start();
    }
}
