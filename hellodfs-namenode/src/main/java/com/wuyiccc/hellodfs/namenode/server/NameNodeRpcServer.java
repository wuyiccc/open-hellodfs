package com.wuyiccc.hellodfs.namenode.server;

/**
 * rpc server, provide rpc service
 *
 * @author wuyiccc
 * @date 2022/4/26 22:05
 */
public class NameNodeRpcServer {

    private FSNameSystem fsNameSystem;

    private DataNodeManager dataNodeManager;

    public NameNodeRpcServer(FSNameSystem fsNameSystem, DataNodeManager dataNodeManager) {
        this.fsNameSystem = fsNameSystem;
        this.dataNodeManager = dataNodeManager;
    }

    /**
     * take over datanode register
     * @param ip
     * @param hostname
     * @return
     */
    public Boolean register(String ip, String hostname) {
        return this.dataNodeManager.register(ip, hostname);
    }

    public Boolean mkdir(String path) throws Exception {
        return this.fsNameSystem.mkdir(path);
    }

    private Boolean heartBeat(String ip, String hostname) {
        return this.dataNodeManager.heartBeat(ip, hostname);
    }

    /**
     * start rpc service
     */
    public void start() {
        System.out.println("start to listen rpc server port, receive data");
    }
}
