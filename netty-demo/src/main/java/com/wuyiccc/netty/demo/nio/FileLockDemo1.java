package com.wuyiccc.netty.demo.nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * @author wuyiccc
 * @date 2022/6/25 20:53
 */
public class FileLockDemo1 {

    public static void main(String[] args) throws IOException, InterruptedException {
        //FileInputStream in = new FileInputStream("/code_learn/test_tmp/filetest.txt");

        RandomAccessFile file = new RandomAccessFile("/code_learn/test_tmp/filetest.txt", "rw");

        FileChannel channel = file.getChannel();
        FileLock lock = channel.lock(0, Integer.MAX_VALUE, false);
        System.out.println("加锁成功");

        //Thread.sleep(60 * 60 * 1000);
        channel.write(ByteBuffer.wrap("abc".getBytes()));
        channel.force(true);


        lock.release();
        channel.close();
        file.close();
    }
}
