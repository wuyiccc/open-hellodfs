package com.wuyiccc.hellodfs.client;


import java.util.concurrent.TimeUnit;

/**
 * @author wuyiccc
 * @date 2022/5/3 9:44
 */
public class FileSystemTest {

    private static FileSystem fileSystem = new FileSystemImpl();

    public static void main(String[] args) throws Exception {
        testCreateFile();
    }


    private static void testMkdir() throws Exception {
        for (int j = 0; j < 10; j++) {
            new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < 100; i++) {
                        try {
                            fileSystem.mkdir("/usr/warehouse/hive" + i + "_" + Thread.currentThread().getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
    }

    private static void testShutdown() throws Exception {
        fileSystem.shutdown();
    }

    private static void testCreateFile() throws Exception {
        fileSystem.upload(null, "/image/product/iphone001.jpg");
    }
}
