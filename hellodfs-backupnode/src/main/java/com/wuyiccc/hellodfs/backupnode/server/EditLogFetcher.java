package com.wuyiccc.hellodfs.backupnode.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * sync from namenode
 *
 * @author wuyiccc
 * @date 2022/5/7 7:21
 */
public class EditLogFetcher extends Thread {

    public static final Integer BACKUP_NODE_FETCH_SIZE = 10;

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

        System.out.println("BackUpNode fetch editsLog starting...");

        while (backupNode.isRunning()) {
            try {
                JSONArray editsLog = this.nameNodeRpcClient.fetchEditsLog();
                if (editsLog.size() == 0) {
                    System.out.println("hasn't fetch editLog, wait 1 second");
                    TimeUnit.SECONDS.sleep(1);
                    continue;
                }

                if (editsLog.size() < BACKUP_NODE_FETCH_SIZE) {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("pull editsLog size <= 10, wait 1 second");
                }

                for (int i = 0; i < editsLog.size(); i++) {
                    JSONObject editLog = editsLog.getJSONObject(i);
                    System.out.println("fetched one editLog" + editLog.toJSONString());
                    String op = editLog.getString("OP");

                    if ("MKDIR".equals(op)) {
                        String path = editLog.getString("PATH");
                        try {
                            this.fsNameSystem.mkdir(path);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
