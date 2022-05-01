package com.wuyiccc.hellodfs.namenode.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * manager datanode cluster
 *
 * @author wuyiccc
 * @date 2022/5/1 18:22
 */
public class DataNodeManager {

    private Map<String, DataNodeInfo> dataNodeMap = new ConcurrentHashMap<>();


    public Boolean register(String ip, String hostname) {
        DataNodeInfo dataNodeInfo = new DataNodeInfo(ip, hostname);
        dataNodeMap.put(ip + "-" + hostname, dataNodeInfo);
        return true;
    }

    public Boolean heartBeat(String ip, String hostname) {
        DataNodeInfo dataNodeInfo = dataNodeMap.get(ip + "-" + hostname);
        dataNodeInfo.setLastHeartBeatTime(System.currentTimeMillis());
        return true;
    }

}
