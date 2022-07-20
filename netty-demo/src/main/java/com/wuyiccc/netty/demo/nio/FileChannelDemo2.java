package com.wuyiccc.netty.demo.nio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author wuyiccc
 * @date 2022/6/25 17:00
 */
public class FileChannelDemo2 {

    public static void main(String[] args) throws FileNotFoundException {

        FileOutputStream out = new FileOutputStream("/code_learn/test_tmp/filetest.txt");
        FileChannel channel = out.getChannel();

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                ByteBuffer buffer = ByteBuffer.wrap("hello word".getBytes());
                try {
                    channel.write(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
