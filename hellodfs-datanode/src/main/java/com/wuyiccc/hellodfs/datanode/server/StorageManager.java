package com.wuyiccc.hellodfs.datanode.server;

import java.io.File;

/**
 * @author wuyiccc
 * @date 2022/5/18 22:36
 */
public class StorageManager {

    public StorageInfo getStorageInfo() {
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

    public void scanFiles(File dir, StorageInfo storageInfo) {
        if (dir.isFile()) {
            String path = dir.getPath();
            path = path.substring(DataNodeConfig.DATA_DIR.length());
            path = path.replace("\\", "/");

            storageInfo.addFilename(path + "_" + dir.length());
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

    public static void main(String[] args) {
        String path = "E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\image\\tmp2";
        System.out.println(path);
        String s1 = path.replace("\\\\", "/");
        System.out.println(s1);
        String s2 = path.replace("\\", "/");
        System.out.println(s2);
    }
}
