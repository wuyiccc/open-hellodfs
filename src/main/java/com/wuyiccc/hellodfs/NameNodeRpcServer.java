package com.wuyiccc.hellodfs;

/**
 * rpc server, provide rpc service
 *
 * @author wuyiccc
 * @date 2022/4/26 22:05
 */
public class NameNodeRpcServer {

    private FSNameSystem fsNameSystem;

    public NameNodeRpcServer (FSNameSystem fsNameSystem) {
        this.fsNameSystem = fsNameSystem;
    }

    public Boolean mkdir(String path) throws Exception {
        return this.fsNameSystem.mkdir(path);
    }

    /**
     * start rpc service
     */
    public void start() {
        System.out.println("start to listen rpc server port, receive data");

    }
}
