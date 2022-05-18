package com.wuyiccc.hellodfs.datanode.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatResponse;

import java.util.concurrent.TimeUnit;

/**
 * @author wuyiccc
 * @date 2022/5/18 22:43
 */
public class HeartBeatManager {


    private NameNodeRpcClient nameNodeRpcClient;
    private StorageManager storageManager;

    public HeartBeatManager(NameNodeRpcClient nameNodeRpcClient, StorageManager storageManager) {
        this.nameNodeRpcClient = nameNodeRpcClient;
        this.storageManager = storageManager;
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
            try {
                while (true) {
                    System.out.println("send rpc request to namenode for heartbeat.......");

                    HeartBeatResponse response = nameNodeRpcClient.heartBeat();

                    System.out.println("heartbeat thread accept namenode response data: " + response.getStatus());


                    // if heartBeat failed
                    if (response.getStatus() == 2) {
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

                    TimeUnit.SECONDS.sleep(30);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
