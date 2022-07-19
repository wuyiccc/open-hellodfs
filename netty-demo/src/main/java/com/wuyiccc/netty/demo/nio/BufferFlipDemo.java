package com.wuyiccc.netty.demo.nio;

import java.nio.ByteBuffer;

/**
 * @author wuyiccc
 * @date 2022/6/25 15:30
 */
public class BufferFlipDemo {


    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(100);
        System.out.println("position=" + buffer.position());
        System.out.println("capacity=" + buffer.capacity());
        System.out.println("limit=" + buffer.limit());


        System.out.println("------------------------");

        byte[] src = new byte[]{1, 2, 3, 4, 5};
        buffer.put(src);
        System.out.println("position=" + buffer.position());


        System.out.println("------------------------");


        buffer.position(0);
        buffer.put(Byte.parseByte("" + 15));
        buffer.put(Byte.parseByte("" + 16));
        buffer.put(Byte.parseByte("" + 17));
        // position = 3

        // limit = 3, position = 0
        buffer.flip();

        byte[] dst = new byte[3];
        // 默认会从buffer中读取3条数据, 如果dst的长度是5, 那么默认会读取5条数据
        buffer.get(dst);

        for (int i = 0; i < dst.length; i++) {
            System.out.print(dst[i]);
            System.out.print(",");
        }
        System.out.println("----------");

        //buffer.get();
        //buffer.position(0);
        buffer.rewind();

        buffer.get();

    }
}
