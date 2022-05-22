package com.wuyiccc.hellodfs.client;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

/**
 * @author wuyiccc
 * @date 2022/5/3 9:44
 */
public class FileSystemTest {

    private static FileSystem fileSystem = new FileSystemImpl();

    public static void main(String[] args) throws Exception {
        //testCreateFile();
        //testShutdown();
        testReadFile();
    }


    private static void testMkdir() throws Exception {
        for (int j = 0; j < 10; j++) {
            new Thread(() -> {
                for (int i = 0; i < 100; i++) {
                    try {
                        fileSystem.mkdir("/usr/warehouse/hive" + i + "_" + Thread.currentThread().getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private static void testShutdown() throws Exception {
        fileSystem.shutdown();
    }

    private static void testCreateFile() throws Exception {
        File image = new File("E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\test\\lingyu.jpg");
        long imageLength = image.length();

        System.out.println("upload file size: " + imageLength + " bytes");

        ByteBuffer buffer = ByteBuffer.allocate((int)imageLength);

        FileInputStream imageIn = new FileInputStream(image);
        FileChannel imageChannel = imageIn.getChannel();
        int hasRead = imageChannel.read(buffer);



        System.out.println("already read: " + hasRead + " bytes data into memory.");

        buffer.flip();
        byte[] imageBytes = buffer.array();

        fileSystem.upload(imageBytes, "/image/product/lingyu.jpg", imageLength);

        imageIn.close();
        imageChannel.close();
    }

    private static void testReadFile() throws Exception {
        byte[] image = fileSystem.download("/image/product/lingyu.jpg");
        ByteBuffer buffer = ByteBuffer.wrap(image);

        FileOutputStream imageOut = new FileOutputStream("E:\\code_learn\\031-opensource\\06-hellodfs\\hellodfs\\test\\lingyu-copy.jpg");

        FileChannel imageChannel = imageOut.getChannel();
        imageChannel.write(buffer);
        imageChannel.close();
        imageOut.close();
    }
}
