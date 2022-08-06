package com.wuyiccc.netty.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * @author wuyiccc
 * @date 2022/6/28 22:23
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private ByteBuf requestBuffer;

    public NettyClientHandler() {
        byte[] requestBytes = "你好, 我发送第一条消息".getBytes(StandardCharsets.UTF_8);
        requestBuffer = Unpooled.buffer(requestBytes.length);
        requestBuffer.writeBytes(requestBytes);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       ctx.writeAndFlush(requestBuffer);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf responseBuffer = (ByteBuf) msg;
        byte[] responseBytes = new byte[responseBuffer.readableBytes()];
        responseBuffer.readBytes(responseBytes);

        String response = new String(responseBytes, StandardCharsets.UTF_8);
        System.out.println("接收到服务端的响应: " + response);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
