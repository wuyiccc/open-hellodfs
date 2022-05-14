package com.wuyiccc.hellodfs.backupnode.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.sql.Timestamp;
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

    public EditLogFetcher(BackupNode backupNode, FSNameSystem fsNameSystem, NameNodeRpcClient nameNodeRpcClient) {
        this.backupNode = backupNode;
        this.nameNodeRpcClient = nameNodeRpcClient;
        this.fsNameSystem = fsNameSystem;
    }

    @Override
    public void run() {

        System.out.println("BackUpNode fetch editsLog starting...");

        while (backupNode.isRunning()) {
            try {

                if (!this.fsNameSystem.isFinishedRecovered()) {
                    System.out.println("hasn't finished metadata recover, jump fetch editslog");
                    TimeUnit.SECONDS.sleep(1);
                    continue;
                }
                long syncedTxId = this.fsNameSystem.getSyncedTxId();
                JSONArray editsLog = this.nameNodeRpcClient.fetchEditsLog(syncedTxId);
                if (editsLog.size() == 0) {
                    TimeUnit.SECONDS.sleep(1);
                    continue;
                }

                if (editsLog.size() < BACKUP_NODE_FETCH_SIZE) {
                    TimeUnit.SECONDS.sleep(1);
                }

                for (int i = 0; i < editsLog.size(); i++) {
                    JSONObject editLog = editsLog.getJSONObject(i);
                    System.out.println("fetched one editLog" + editLog.toJSONString());
                    String op = editLog.getString("OP");

                    if ("MKDIR".equals(op)) {
                        String path = editLog.getString("PATH");
                        try {
                            this.fsNameSystem.mkdir(editLog.getLongValue("txId"), path);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if ("CREATE".equals(op)) {
                        String filename = editLog.getString("PATH");
                        try {
                            this.fsNameSystem.create(filename);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                this.nameNodeRpcClient.setIsNameNodeRunning(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
                this.nameNodeRpcClient.setIsNameNodeRunning(false);
            }

        }
    }
}
