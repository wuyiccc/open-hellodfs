package com.wuyiccc.hellodfs.client;

/**
 * @author wuyiccc
 * @date 2022/5/3 9:22
 */
public interface FileSystem {

    public void mkdir(String path) throws Exception;


    /**
     * graceful close
     * @throws Exception
     */
    public void shutdown() throws Exception;

}
