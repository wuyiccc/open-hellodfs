package com.wuyiccc.netty.demo.nio;

import javax.xml.crypto.Data;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author wuyiccc
 * @date 2022/6/25 17:43
 */
public class FileChannelDemo3 {

    public static void main(String[] args) throws IOException {

        FileInputStream in = new FileInputStream("/code_learn/test_tmp/filetest.txt");
        FileChannel channel = in.getChannel();

        ByteBuffer buffer = ByteBuffer.allocateDirect(8);
        channel.read(buffer);

        buffer.flip();
        byte[] data = new byte[8];
        buffer.get(data);


        System.out.println(new String(data));
        channel.close();
        in.close();
    }
}
