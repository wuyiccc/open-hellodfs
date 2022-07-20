package com.wuyiccc.netty.demo.nio;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author wuyiccc
 * @date 2022/6/25 15:55
 */
public class FileChannelDemo1 {

    public static void main(String[] args) throws IOException {
        FileOutputStream out = new FileOutputStream("/code_learn/test_tmp/filetest.txt");
        FileChannel channel = out.getChannel();
        ByteBuffer buffer = ByteBuffer.wrap("hello world".getBytes());
        channel.write(buffer);

        // 随机写需要调整position的位置
        channel.position(channel.position() + 1);

        buffer.rewind();
        // 顺序写
        channel.write(buffer);



        channel.close();
        out.close();
    }
}
