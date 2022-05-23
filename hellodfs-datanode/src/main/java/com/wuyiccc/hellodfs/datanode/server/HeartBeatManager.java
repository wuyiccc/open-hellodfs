package com.wuyiccc.hellodfs.datanode.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatResponse;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author wuyiccc
 * @date 2022/5/18 22:43
 */
public class HeartBeatManager {


    public static final Integer SUCCESS = 1;

    public static final Integer FAILURE = 2;

    public static final Integer COMMAND_REGISTER = 1;
    public static final Integer COMMAND_REPORT_COMPLETE_STORAGE_INFO = 2;
    public static final Integer COMMAND_REPLICATE = 3;
    public static final Integer COMMAND_REMOVE_REPLICA = 4;


    private NameNodeRpcClient nameNodeRpcClient;
    private StorageManager storageManager;

    private ReplicateManager replicateManager;

    public HeartBeatManager(NameNodeRpcClient nameNodeRpcClient, StorageManager storageManager, ReplicateManager replicateManager) {
        this.nameNodeRpcClient = nameNodeRpcClient;
        this.storageManager = storageManager;
        this.replicateManager = replicateManager;
    }


    public void start() {
        new HeartBeatThread().start();
    }

    /**
     * send heartbeat to namenode thread
     */
    class HeartBeatThread extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println("send rpc request to namenode for heartbeat.......");

                    HeartBeatResponse response = nameNodeRpcClient.heartBeat();

                    System.out.println("heartbeat thread accept namenode response data: " + response.getStatus());

                    if (SUCCESS.equals(response.getStatus())) {
                        System.out.println("commands: " + response.getCommands());
                        JSONArray commands = JSON.parseArray(response.getCommands());

                        if (commands.size() > 0) {
                            for (int i = 0; i < commands.size(); i++) {
                                JSONObject command = commands.getJSONObject(i);
                                Integer type = command.getInteger("type");
                                JSONObject task = command.getJSONObject("content");

                                if (type.equals(COMMAND_REPLICATE)) {
                                    replicateManager.addReplicateTask(task);
                                } else if (type.equals(COMMAND_REMOVE_REPLICA)) {
                                    String filename = task.getString("filename");
                                    String absoluteFilename = FileUtils.getAbsoluteFilename(filename);
                                    File file = new File(absoluteFilename);
                                    if(file.exists()) {
                                        file.delete();
                                    }
                                }

                            }
                        }
                    }

                    // if heartBeat failed
                    if (FAILURE.equals(response.getStatus())) {
                        JSONArray commands = JSONArray.parseArray(response.getCommands());

                        for (int i = 0; i < commands.size(); i++) {
                            JSONObject command = commands.getJSONObject(i);
                            Integer type = command.getInteger("type");

                            // if this command means register
                            if (type.equals(1)) {
                                nameNodeRpcClient.register();
                            } else if (type.equals(2)) {
                                // if this command means report all storageInfo
                                StorageInfo storageInfo = storageManager.getStorageInfo();
                                if (storageInfo != null) {
                                    nameNodeRpcClient.reportAllStorageInfo(storageInfo);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("current namenode cannot use, heartbeat fail...");
                    e.printStackTrace();
                }

                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
