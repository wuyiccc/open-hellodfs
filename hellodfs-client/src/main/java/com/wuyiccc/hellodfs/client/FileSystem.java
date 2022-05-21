package com.wuyiccc.hellodfs.client;

/**
 * @author wuyiccc
 * @date 2022/5/3 9:22
 */
public interface FileSystem {

    public void mkdir(String path) throws Exception;


    /**
     * graceful close
     *
     * @throws Exception
     */
    public void shutdown() throws Exception;


    /**
     * upload file
     *
     * @param file
     * @param filename
     * @param fileSize
     * @throws Exception
     */
    Boolean upload(byte[] file, String filename, long fileSize) throws Exception;


    byte[] download(String filename) throws Exception;

}
