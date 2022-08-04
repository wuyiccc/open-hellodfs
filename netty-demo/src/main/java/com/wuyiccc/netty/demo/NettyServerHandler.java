package com.wuyiccc.netty.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.nio.charset.StandardCharsets;

/**
 * @author wuyiccc
 * @date 2022/6/28 21:29
 */
public class NettyServerHandler extends ChannelHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf requestBuffer = (ByteBuf) msg;
        byte[] requestBytes = new byte[requestBuffer.readableBytes()];
        requestBuffer.readBytes(requestBytes);

        String request = new String(requestBytes, StandardCharsets.UTF_8);
        System.out.println("接收到请求: " + request);

        String response = "收到你的请求了, 返回响应给你";
        ByteBuf responseBuffer = Unpooled.copiedBuffer(response.getBytes());
        ctx.write(responseBuffer);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
