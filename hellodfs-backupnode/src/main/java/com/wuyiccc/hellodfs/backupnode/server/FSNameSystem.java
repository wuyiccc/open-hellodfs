package com.wuyiccc.hellodfs.backupnode.server;

/**
 *
 * The core components responsible for managing metadata
 *
 * @author wuyiccc
 * @date 2022/4/26 22:11
 */
public class FSNameSystem {


    private FSDirectory fsDirectory;


    public FSNameSystem() {
        this.fsDirectory = new FSDirectory();
    }


    /**
     * create a directory
     * @param path the directory path
     * @return create success or fail
     */
    public Boolean mkdir(String path) throws Exception {
        this.fsDirectory.mkdir(path);
        return true;
    }

}