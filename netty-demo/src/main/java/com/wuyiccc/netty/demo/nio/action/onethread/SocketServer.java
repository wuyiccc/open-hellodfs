package com.wuyiccc.netty.demo.nio.action.onethread;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author wuyiccc
 * @date 2022/6/26 10:53
 */
public class SocketServer {

    public static void main(String[] args) throws Exception{


        ServerSocket serverSocket = new ServerSocket(9000);
        Socket socket = serverSocket.accept();

        InputStreamReader in = new InputStreamReader(socket.getInputStream());
        OutputStream out = socket.getOutputStream();

        char[] buf = new char[1024 * 1024];
        int len = in.read(buf);

        while (len != -1) {
            String request = new String(buf, 0, len);
            System.out.println("服务端收到了请求:" + request);
            out.write("收到, 收到".getBytes());

            len = in.read(buf);
        }

        out.close();
        in.close();
        socket.close();
        serverSocket.close();
    }
}
