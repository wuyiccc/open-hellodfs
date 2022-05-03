package com.wuyiccc.hellodfs.client;


/**
 * @author wuyiccc
 * @date 2022/5/3 9:44
 */
public class FileSystemTest {

    public static void main(String[] args) throws Exception {
        FileSystem fileSystem = new FileSystemImpl();
        fileSystem.mkdir("/usr/warehouse/hive");
        fileSystem.mkdir("/usr/warehouse/spark");
        fileSystem.mkdir("/usr/log/access");
        fileSystem.mkdir("/data/old");
    }
}
