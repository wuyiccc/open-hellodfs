package com.wuyiccc.hellodfs.backupnode.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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

    private FSNameSystem fsNameSystem;

    public EditLogFetcher(BackupNode backupNode, FSNameSystem fsNameSystem) {
        this.backupNode = backupNode;
        this.nameNodeRpcClient = new NameNodeRpcClient();
        this.fsNameSystem = fsNameSystem;
    }

    @Override
    public void run() {
        while(backupNode.isRunning()) {
            JSONArray editsLogs = this.nameNodeRpcClient.fetchEditsLog();
            for(int i = 0; i < editsLogs.size(); i++) {
                JSONObject editLog = editsLogs.getJSONObject(i);
                String op = editLog.getString("OP");

                if("MKDIR".equals(op)) {
                    String path = editLog.getString("PATH");
                    try {
                        this.fsNameSystem.mkdir(path);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
