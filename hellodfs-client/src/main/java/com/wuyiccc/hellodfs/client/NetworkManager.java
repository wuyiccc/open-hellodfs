package com.wuyiccc.hellodfs.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author wuyiccc
 * @date 2022/5/28 16:46
 */
public class NetworkManager {


    /**
     * is connecting
     */
    public static final Integer CONNECTING = 1;

    /**
     * already connected
     */
    public static final Integer CONNECTED = 2;

    private Selector selector;

    /**
     * all connect
     */
    private Map<String, SocketChannel> connections;


    /**
     * the status of each connection
     */
    private Map<String, Integer> connectState;

    /**
     * A machine waiting to establish a connection
     */
    private ConcurrentLinkedQueue<Host> waitingConnectHosts;

    public NetworkManager() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.connections = new ConcurrentHashMap<>();
        this.connectState = new ConcurrentHashMap<>();
        this.waitingConnectHosts = new ConcurrentLinkedQueue<>();

        new NetworkPollThread().start();
    }

    /**
     * Attempt to connect to the specified dataNode
     */
    public void maybeConnect(String hostname, Integer nioPort) throws Exception {
        synchronized(this) {
            if(!connectState.containsKey(hostname)) {
                connectState.put(hostname, CONNECTING);
                waitingConnectHosts.offer(new Host(hostname, nioPort));
            }
            while(connectState.get(hostname).equals(CONNECTING)) {
                wait(100);
            }
        }
    }

    /**
     * Attempt to initiate a connection request from the queued machine
     */
    private void tryConnect() {
        try {
            Host host = null;
            SocketChannel channel = null;

            while((host = waitingConnectHosts.poll()) != null) {
                channel = SocketChannel.open();
                channel.configureBlocking(false);
                channel.connect(new InetSocketAddress(host.hostname, host.nioPort));
                channel.register(selector, SelectionKey.OP_CONNECT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class NetworkPollThread extends Thread {

        @Override
        public void run() {
            while(true) {
                tryConnect();
            }
        }

    }

    /**
     * represent a machine
     */
    class Host {

        String hostname;
        Integer nioPort;

        public Host(String hostname, Integer nioPort) {
            this.hostname = hostname;
            this.nioPort = nioPort;
        }
    }

}
