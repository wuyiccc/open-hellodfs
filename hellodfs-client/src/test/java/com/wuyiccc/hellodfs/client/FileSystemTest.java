package com.wuyiccc.hellodfs.client;


/**
 * @author wuyiccc
 * @date 2022/5/3 9:44
 */
public class FileSystemTest {

    public static void main(String[] args) throws Exception {
        FileSystem fileSystem = new FileSystemImpl();
        fileSystem.mkdir("/usr/local/kafka/data");
    }
}
