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

    public Boolean register(String ip, String hostname, int nioPort) {

        // if dataNodeMap contains this datanode, this means the datanode has been registered before and does not need to be re-registered
        if (this.dataNodeMap.containsKey(ip + "-" + hostname)) {
            System.out.println("datanode register failed, this datanode already exist");
            return false;
        }

        DataNodeInfo dataNodeInfo = new DataNodeInfo(ip, hostname, nioPort);
        dataNodeMap.put(ip + "-" + hostname, dataNodeInfo);
        System.out.println("DataNode register ip= " + ip + ", hostname= " + hostname + ", nioPort=" + nioPort);
        return true;
    }

    public Boolean heartBeat(String ip, String hostname) {
        DataNodeInfo dataNodeInfo = dataNodeMap.get(ip + "-" + hostname);
        if (dataNodeInfo == null) {
            System.out.println("heartbeat fail, datanode need register again...");
            return false;
        }
        dataNodeInfo.setLastHeartBeatTime(System.currentTimeMillis());
        System.out.println("DataNode heartBeat ip= " + ip + ", hostname= " + hostname);
        return true;
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
            if (dataNodeInfoList.size() >= 2) {
                selectedDataNodeList.add(dataNodeInfoList.get(0));
                selectedDataNodeList.add(dataNodeInfoList.get(1));
                // update dataNodeInfo file size
                dataNodeInfoList.get(0).addStoredDataSize(fileSize);
                dataNodeInfoList.get(1).addStoredDataSize(fileSize);
            }
            return selectedDataNodeList;
        }
    }


    public DataNodeInfo getDataNodeInfo(String ip, String hostname) {
        return dataNodeMap.get(ip + "-" + hostname);
    }

    public void setStoredDataSize(String ip, String hostname, Long storedDataSize) {
        // datanode registration takes precedence over report
        DataNodeInfo dataNodeInfo = dataNodeMap.get(ip + "-" + hostname);
        dataNodeInfo.setStoredDataSize(storedDataSize);
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
                            System.out.println("datanodes: " + toRemoveDataNode + ", hearbeat is down....");
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
