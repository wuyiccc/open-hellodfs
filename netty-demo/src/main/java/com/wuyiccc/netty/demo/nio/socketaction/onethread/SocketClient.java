package com.wuyiccc.netty.demo.nio.socketaction.onethread;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author wuyiccc
 * @date 2022/6/26 15:40
 */
public class SocketClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9000);
        InputStreamReader in = new InputStreamReader(socket.getInputStream());
        OutputStream out = socket.getOutputStream();

        out.write("你好".getBytes());
        char[] buf = new char[1024 * 1024];
        int len = in.read(buf);

        while (len != -1) {
            String response = new String(buf, 0, len);
            System.out.println("客户端收到了响应: " + response);
            len = in.read(buf);
        }

        in.close();
        out.close();
        socket.close();
    }
}
