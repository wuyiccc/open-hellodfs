package com.wuyiccc.netty.demo.nio.socketaction.multithread;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author wuyiccc
 * @date 2022/6/26 16:11
 */
public class SocketClient {
    public static void main(String[] args) {

        for (int i = 0; i < 1; i++) {
            new Thread() {
                @Override
                public void run() {

                    try {
                        Socket socket = new Socket("localhost", 9000);

                        InputStreamReader in = new InputStreamReader(socket.getInputStream());
                        OutputStream out = socket.getOutputStream();

                        out.write("你好".getBytes());
                        char[] buf = new char[1024 * 1024];
                        int len = in.read(buf);

                        if (len != -1) {
                            String response = new String(buf, 0, len);
                            System.out.println("[" + Thread.currentThread().getName() + "]客户端收到了响应: " + response);
                        }
                        in.close();
                        out.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
}
