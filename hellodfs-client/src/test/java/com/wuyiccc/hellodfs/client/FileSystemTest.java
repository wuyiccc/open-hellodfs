package com.wuyiccc.hellodfs.client;


/**
 * @author wuyiccc
 * @date 2022/5/3 9:44
 */
public class FileSystemTest {

    public static void main(String[] args) throws Exception {
        FileSystem fileSystem = new FileSystemImpl();

        for (int j = 0; j < 4; j++) {
            new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < 5; i++) {
                        try {
                            fileSystem.mkdir("/usr/warehouse/hive" + i + Thread.currentThread().getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
    }
}
