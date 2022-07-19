package com.wuyiccc.netty.demo.nio;

import javafx.geometry.Pos;

import java.nio.ByteBuffer;

/**
 * @author wuyiccc
 * @date 2022/6/25 15:10
 */
public class BufferDemo2 {

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


        // 读数据时需要将position的位置置为开始的位置
        buffer.position(0);
        byte[] dst = new byte[5];
        buffer.get(dst);
        System.out.println("position=" + buffer.position());

        System.out.print("dst=[");


        for (int i = 0; i < dst.length; i++) {
            System.out.print(dst[i]);
            if (i != dst.length - 1) {
                System.out.print(",");
            }
        }

        System.out.println("]");


    }
}
