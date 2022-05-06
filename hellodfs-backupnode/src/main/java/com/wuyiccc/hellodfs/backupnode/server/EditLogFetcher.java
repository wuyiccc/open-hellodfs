package com.wuyiccc.hellodfs.backupnode.server;

import com.alibaba.fastjson.JSONArray;

/**
 *
 * sync from namenode
 *
 * @author wuyiccc
 * @date 2022/5/7 7:21
 */
public class EditLogFetcher extends Thread {

    private BackupNode backupNode;

    private NameNodeRpcClient nameNodeRpcClient;

    public EditLogFetcher(BackupNode backupNode) {
        this.backupNode = backupNode;
        this.nameNodeRpcClient = new NameNodeRpcClient();
    }

    @Override
    public void run() {
        while (backupNode.isRunning()) {
            JSONArray editsLogs = this.nameNodeRpcClient.fetchEditsLog();
        }
    }
}
