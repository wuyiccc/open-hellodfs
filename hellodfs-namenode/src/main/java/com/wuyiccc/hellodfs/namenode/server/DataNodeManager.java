package com.wuyiccc.hellodfs.namenode.server;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * manager datanode cluster
 *
 * @author wuyiccc
 * @date 2022/5/1 18:22
 */
public class DataNodeManager {

    private Map<String, DataNodeInfo> dataNodeMap = new ConcurrentHashMap<>();


    public DataNodeManager() {
        new DataNodeAliveMonitor().start();
    }

    public Boolean register(String ip, String hostname) {
        DataNodeInfo dataNodeInfo = new DataNodeInfo(ip, hostname);
        dataNodeMap.put(ip + "-" + hostname, dataNodeInfo);
        System.out.println("DataNode register ip= " + ip + ", hostname= " + hostname);
        return true;
    }

    public Boolean heartBeat(String ip, String hostname) {
        DataNodeInfo dataNodeInfo = dataNodeMap.get(ip + "-" + hostname);
        if (dataNodeInfo != null) {
            dataNodeInfo.setLastHeartBeatTime(System.currentTimeMillis());
            System.out.println("DataNode heartBeat ip= " + ip + ", hostname= " + hostname);
            return true;
        }
        return false;
    }

    /**
     * get the two smallest dataNodes and update the storedDataSize
     */
    public List<DataNodeInfo> allocateDataNodes(long fileSize) {
        synchronized (this) {
            List<DataNodeInfo> dataNodeInfoList = new ArrayList<>();
            for (DataNodeInfo dataNodeInfo : dataNodeMap.values()) {
                dataNodeInfoList.add(dataNodeInfo);
            }

            // sort min to max
            Collections.sort(dataNodeInfoList);

            List<DataNodeInfo> selectedDataNodeList = new ArrayList<>();
            if (selectedDataNodeList.size() >= 2) {
                selectedDataNodeList.add(dataNodeInfoList.get(0));
                selectedDataNodeList.add(dataNodeInfoList.get(1));
                // update dataNodeInfo file size
                dataNodeInfoList.get(0).addStoredDataSize(fileSize);
                dataNodeInfoList.get(1).addStoredDataSize(fileSize);
            }
            return  selectedDataNodeList;
        }
    }

    /**
     * check datanode is alive
     */
    class DataNodeAliveMonitor extends Thread {

        @Override
        public void run() {

            while (true) {

                try {
                    List<String> toRemoveDataNodes = new ArrayList<>();

                    Iterator<DataNodeInfo> dataNodeInfoIterator = dataNodeMap.values().iterator();
                    DataNodeInfo dataNodeInfo = null;

                    while (dataNodeInfoIterator.hasNext()) {
                        dataNodeInfo = dataNodeInfoIterator.next();
                        if (System.currentTimeMillis() - dataNodeInfo.getLastHeartBeatTime() > 90 * 1000) {
                            toRemoveDataNodes.add(dataNodeInfo.getIp() + "-" + dataNodeInfo.getHostname());
                        }
                    }
                    if (!toRemoveDataNodes.isEmpty()) {
                        for (String toRemoveDataNode : toRemoveDataNodes) {
                            dataNodeMap.remove(toRemoveDataNode);
                        }
                    }

                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }
    }

}
