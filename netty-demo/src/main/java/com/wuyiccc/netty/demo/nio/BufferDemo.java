package com.wuyiccc.netty.demo.nio;

import java.nio.ByteBuffer;

/**
 * @author wuyiccc
 * @date 2022/6/25 11:08
 */
public class BufferDemo {

    public static void main(String[] args) {

        byte[] data = new byte[]{0, 1, 2, 3, 4};
        ByteBuffer buffer = ByteBuffer.wrap(data);

        System.out.println(buffer.capacity());
        System.out.println(buffer.position());
        System.out.println(buffer.limit());

        System.out.println("----------------------------");

        // 读取当前position的数据
        System.out.println(buffer.get());
        System.out.println(buffer.capacity());
        System.out.println(buffer.position());
        // 标记此时position的位置
        buffer.mark();
        System.out.println(buffer.limit());

        System.out.println("--------------------------");

        // 将position的位置变更为index=3
        buffer.position(3);
        System.out.println(buffer.get());
        System.out.println(buffer.position());
        // 重置position的位置为mark存储的位置
        buffer.reset();
        System.out.println(buffer.position());
    }
}
