package com.wuyiccc.netty.demo.nio.socketaction.multithread;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author wuyiccc
 * @date 2022/6/26 16:05
 */
public class SocketServer {

    public static void main(String[] args) throws Exception{
        ServerSocket serverSocket = new ServerSocket(9000);

        while (true) {

            Socket accept = serverSocket.accept();
            new Worker(accept).start();
        }
    }

    static class Worker extends Thread {
        Socket socket;

        public Worker(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                InputStreamReader in = new InputStreamReader(socket.getInputStream());
                OutputStream out = socket.getOutputStream();
                char[] buf = new char[1024 * 1024];
                int len = in.read(buf);
                if (len != -1) {
                    String request = new String(buf, 0, len);
                    System.out.println("[" + Thread.currentThread().getName() + "]服务端收到了请求: " + request);
                    out.write("收到, 收到".getBytes());
                }
                out.close();
                in.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
