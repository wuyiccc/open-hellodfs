package com.wuyiccc.hellodfs.backupnode.server;

import java.util.concurrent.TimeUnit;

/**
 * @author wuyiccc
 * @date 2022/5/7 7:22
 */
public class BackupNode {

    private volatile Boolean isRunning = true;

    public static void main(String[] args) throws InterruptedException {
        BackupNode backupNode = new BackupNode();
        backupNode.start();
    }


    public void start() throws InterruptedException {
        EditLogFetcher editLogFetcher = new EditLogFetcher(this);
        editLogFetcher.start();
    }

    public void run() throws InterruptedException {
        while (isRunning) {
            TimeUnit.SECONDS.sleep(1);
        }
    }

    public Boolean isRunning() {
        return isRunning;
    }
}
