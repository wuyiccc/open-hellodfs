package com.wuyiccc.hellodfs.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

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

    public static final Long POLL_TIMEOUT = 500L;

    private Selector selector;


    /**
     * A machine waiting to establish a connection
     */
    private ConcurrentLinkedQueue<Host> waitingConnectHosts;

    /**
     * all connect
     */
    private Map<String, SocketChannel> connections;

    /**
     * the status of each connection
     */
    private Map<String, Integer> connectState;

    /**
     * key: hostname
     */
    private Map<String, ConcurrentLinkedQueue<NetworkRequest>> waitingRequests;


    public NetworkManager() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.connections = new ConcurrentHashMap<>();
        this.connectState = new ConcurrentHashMap<>();
        this.waitingConnectHosts = new ConcurrentLinkedQueue<>();
        this.waitingRequests = new ConcurrentHashMap<>();

        new NetworkPollThread().start();
    }

    /**
     * Attempt to connect to the specified dataNode
     */
    public void maybeConnect(String hostname, Integer nioPort) throws Exception {
        synchronized (this) {
            if (!connectState.containsKey(hostname)) {
                connectState.put(hostname, CONNECTING);
                waitingConnectHosts.offer(new Host(hostname, nioPort));
            }
            while (connectState.get(hostname).equals(CONNECTING)) {
                wait(100);
            }
        }
    }

    public void sendRequest(NetworkRequest request) {
        ConcurrentLinkedQueue<NetworkRequest> requestQueue = waitingRequests.get(request.getHostname());
        requestQueue.offer(request);
    }


    class NetworkPollThread extends Thread {

        @Override
        public void run() {
            while (true) {
                tryConnect();
                poll();
            }
        }

        /**
         * Attempt to initiate a connection request from the queued machine
         */
        private void tryConnect() {
            try {
                Host host = null;
                SocketChannel channel = null;

                while ((host = waitingConnectHosts.poll()) != null) {
                    channel = SocketChannel.open();
                    channel.configureBlocking(false);
                    channel.connect(new InetSocketAddress(host.hostname, host.nioPort));
                    channel.register(selector, SelectionKey.OP_CONNECT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void poll() {
            SocketChannel channel = null;

            try {
                // 500ms timeout
                int selectedKeys = selector.select(POLL_TIMEOUT);
                if (selectedKeys <= 0) {
                    return;
                }

                Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
                while (keysIterator.hasNext()) {
                    SelectionKey key = keysIterator.next();
                    keysIterator.remove();

                    if (key.isConnectable()) {
                        channel = (SocketChannel) key.channel();
                        if (channel.isConnectionPending()) {
                            while (!channel.finishConnect()) {
                                TimeUnit.MILLISECONDS.sleep(100);
                            }
                        }

                        System.out.println("completed connect with server......");

                        InetSocketAddress remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
                        // refresh cache
                        waitingRequests.put(remoteAddress.getHostName(), new ConcurrentLinkedQueue<>());
                        connectState.put(remoteAddress.getHostName(), CONNECTED);
                        connections.put(remoteAddress.getHostName(), channel);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

                if (channel != null) {
                    try {
                        channel.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
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
