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

    /**
     * ip-hostname:
     */
    private Map<String, DataNodeInfo> dataNodeMap = new ConcurrentHashMap<>();

    private FSNameSystem fsNameSystem;


    public DataNodeManager() {
        new DataNodeAliveMonitor().start();
    }

    public void setFsNameSystem(FSNameSystem fsNameSystem) {
        this.fsNameSystem = fsNameSystem;
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

    public DataNodeInfo getDataNodeInfo(String id) {
        return dataNodeMap.get(id);
    }


    public void setStoredDataSize(String ip, String hostname, Long storedDataSize) {
        // datanode registration takes precedence over report
        DataNodeInfo dataNodeInfo = dataNodeMap.get(ip + "-" + hostname);
        dataNodeInfo.setStoredDataSize(storedDataSize);
    }

    public DataNodeInfo reallocateDataNode(long fileSize, String excludedDataNodeId) {
        synchronized (this) {

            DataNodeInfo excludedDataNodeInfo = this.dataNodeMap.get(excludedDataNodeId);
            excludedDataNodeInfo.addStoredDataSize(-fileSize);

            List<DataNodeInfo> dataNodeList = new ArrayList<>();
            for (DataNodeInfo dataNodeInfo : dataNodeMap.values()) {
                if (!excludedDataNodeInfo.equals(dataNodeInfo)) {
                    dataNodeList.add(dataNodeInfo);
                }
            }

            Collections.sort(dataNodeList);
            DataNodeInfo selectedDatanode = null;
            if (dataNodeList.size() >= 1) {
                selectedDatanode = dataNodeList.get(0);
                dataNodeList.get(0).addStoredDataSize(fileSize);
            }
            return selectedDatanode;
        }
    }

    public void createReplicaTask(DataNodeInfo dataNodeInfo) {
        List<String> files = this.fsNameSystem.getFilesByDataNode(dataNodeInfo.getIp(), dataNodeInfo.getHostname());

        for (String file : files) {
            System.out.println("need replica filename: " + file);
            String filename = file.split("_")[0];
            Long fileLength = Long.valueOf(file.split("_")[1]);


            // ?????????????????????????????????????????????
            DataNodeInfo sourceDatanode = this.fsNameSystem.getReplicateSource(filename, dataNodeInfo);

            // ?????????????????????????????????
            DataNodeInfo destDatanode = allocateReplicateDataNode(fileLength, sourceDatanode, dataNodeInfo);


            ReplicateTask replicateTask = new ReplicateTask(filename, fileLength, sourceDatanode, destDatanode);

            destDatanode.addReplicateTask(replicateTask);
        }
    }


    public DataNodeInfo allocateReplicateDataNode(long fileSize, DataNodeInfo sourceDataNode, DataNodeInfo deadDataNode) {
        synchronized (this) {
            List<DataNodeInfo> dataNodeInfoList = new ArrayList<>();
            for (DataNodeInfo dataNodeInfo : this.dataNodeMap.values()) {
                if (!dataNodeInfo.equals(sourceDataNode) && !dataNodeInfo.equals(deadDataNode)) {
                    dataNodeInfoList.add(dataNodeInfo);
                }
            }
            Collections.sort(dataNodeInfoList);
            DataNodeInfo selectedDataNode = null;
            if (!dataNodeInfoList.isEmpty()) {
                selectedDataNode = dataNodeInfoList.get(0);
                dataNodeInfoList.get(0).addStoredDataSize(fileSize);
            }
            return selectedDataNode;
        }
    }

    public void createRebalanceTasks() {
        synchronized (this) {

            long totalStoredDataSize = 0;
            for (DataNodeInfo datanode : this.dataNodeMap.values()) {
                totalStoredDataSize += datanode.getStoredDataSize();
            }
            long averageStoredDataSize = totalStoredDataSize / this.dataNodeMap.size();

            List<DataNodeInfo> sourceDataNodeInfoList = new ArrayList<>();
            List<DataNodeInfo> destDataNodeInfoList = new ArrayList<>();

            for (DataNodeInfo dataNodeInfo : this.dataNodeMap.values()) {

                if (dataNodeInfo.getStoredDataSize() > averageStoredDataSize) {
                    sourceDataNodeInfoList.add(dataNodeInfo);
                }

                if (dataNodeInfo.getStoredDataSize() < averageStoredDataSize) {
                    destDataNodeInfoList.add(dataNodeInfo);
                }
            }


            List<RemoveReplicaTask> removeReplicaTasks = new ArrayList<>();

            for (DataNodeInfo sourceDataNodeInfo : sourceDataNodeInfoList) {

                long toRemoveDataSize = sourceDataNodeInfo.getStoredDataSize() - averageStoredDataSize;

                for (DataNodeInfo destDataNodeInfo : destDataNodeInfoList) {

                    if (destDataNodeInfo.getStoredDataSize() + toRemoveDataSize <= averageStoredDataSize) {
                        createRebalanceTasks(sourceDataNodeInfo, destDataNodeInfo, removeReplicaTasks, toRemoveDataSize);
                        break;
                    } else if (destDataNodeInfo.getStoredDataSize() < averageStoredDataSize) {
                        long maxRemoveDataSize = averageStoredDataSize - destDataNodeInfo.getStoredDataSize();
                        long removedDataSize = createRebalanceTasks(sourceDataNodeInfo, destDataNodeInfo, removeReplicaTasks, maxRemoveDataSize);
                        toRemoveDataSize -= removedDataSize;
                    }
                }
            }
            new DelayRemoveReplicaThread(removeReplicaTasks).start();
        }
    }

    private long createRebalanceTasks(DataNodeInfo sourceDataNode, DataNodeInfo destDataNode, List<RemoveReplicaTask> removeReplicaTasks, long maxRemoveDataSize) {
        List<String> files = this.fsNameSystem.getFilesByDataNodeInfo(sourceDataNode.getIp(), sourceDataNode.getHostname());

        long removedDataSize = 0;

        for (String file : files) {
            String filename = file.split("_")[0];
            long fileLength = Long.parseLong(file.split("_")[1]);

            if (removedDataSize + fileLength >= maxRemoveDataSize) {
                break;
            }

            ReplicateTask replicateTask = new ReplicateTask(filename, fileLength, sourceDataNode, destDataNode);
            destDataNode.addReplicateTask(replicateTask);
            destDataNode.addStoredDataSize(fileLength);

            sourceDataNode.addStoredDataSize(-fileLength);
            this.fsNameSystem.removeReplicaFromDataNode(sourceDataNode.getId(), file);
            RemoveReplicaTask removeReplicaTask = new RemoveReplicaTask(filename, sourceDataNode);
            removeReplicaTasks.add(removeReplicaTask);
            removedDataSize += fileLength;
        }

        return removedDataSize;
    }

    class DelayRemoveReplicaThread extends Thread {

        private List<RemoveReplicaTask> removeReplicaTasks;

        public DelayRemoveReplicaThread(List<RemoveReplicaTask> removeReplicaTasks) {
            this.removeReplicaTasks = removeReplicaTasks;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();

            while (true) {
                try {
                    long now = System.currentTimeMillis();

                    // 24h
                    if (now - start > 24 * 60 * 60 * 1000) {
                        for (RemoveReplicaTask removeReplicaTask : removeReplicaTasks) {
                            removeReplicaTask.getDataNodeInfo().addRemoveReplicaTask(removeReplicaTask);
                        }
                        break;
                    }
                    TimeUnit.SECONDS.sleep(60);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
                    List<DataNodeInfo> toRemoveDataNodes = new ArrayList<>();

                    Iterator<DataNodeInfo> dataNodeInfoIterator = dataNodeMap.values().iterator();
                    DataNodeInfo dataNodeInfo = null;

                    while (dataNodeInfoIterator.hasNext()) {
                        dataNodeInfo = dataNodeInfoIterator.next();
                        if (System.currentTimeMillis() - dataNodeInfo.getLastHeartBeatTime() > 60 * 1000) {
                            toRemoveDataNodes.add(dataNodeInfo);
                        }
                    }
                    if (!toRemoveDataNodes.isEmpty()) {
                        for (DataNodeInfo toRemoveDataNode : toRemoveDataNodes) {

                            createReplicaTask(toRemoveDataNode);

                            dataNodeMap.remove(toRemoveDataNode.getId());
                            System.out.println("datanodes: " + toRemoveDataNode + ", hearbeat is down....");

                            fsNameSystem.removeDeadDataNode(toRemoveDataNode);
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
