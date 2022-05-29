package com.wuyiccc.hellodfs.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
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

    public static final Integer DISCONNECTED = 3;

    public static final Integer RESPONSE_SUCCESS = 1;

    public static final Integer RESPONSE_FAILURE = 2;

    public static final Long POLL_TIMEOUT = 500L;

    private Selector selector;

    /**
     * the status of each connection (already completed connect and wait to connect)
     */
    private Map<String, Integer> connectState;

    /**
     * A machine waiting to establish a connection
     */
    private ConcurrentLinkedQueue<Host> waitingConnectHosts;

    /**
     * all connect (already completed connected)
     */
    private Map<String, SelectionKey> connections;


    /**
     * wait to send request
     * key: hostname
     */
    private Map<String, ConcurrentLinkedQueue<NetworkRequest>> waitingRequests;

    /**
     * prepare to send request
     * key: hostname
     */
    private Map<String, NetworkRequest> toSendRequests;

    private Map<String, NetworkResponse> finishedResponses;

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
        this.toSendRequests = new ConcurrentHashMap<>();
        this.finishedResponses = new ConcurrentHashMap<>();

        new NetworkPollThread().start();
    }

    /**
     * Attempt to connect to the specified dataNode
     */
    public Boolean maybeConnect(String hostname, Integer nioPort) {
        synchronized (this) {
            if (!connectState.containsKey(hostname) || connectState.get(hostname).equals(DISCONNECTED)) {
                connectState.put(hostname, CONNECTING);
                waitingConnectHosts.offer(new Host(hostname, nioPort));
            }

            while (connectState.get(hostname).equals(CONNECTING)) {
                try {
                    wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (connectState.get(hostname).equals(DISCONNECTED)) {
                return false;
            }

            return true;
        }
    }

    public void sendRequest(NetworkRequest request) {
        ConcurrentLinkedQueue<NetworkRequest> requestQueue = waitingRequests.get(request.getHostname());
        requestQueue.offer(request);
    }

    public NetworkResponse waitResponse(String requestId) throws InterruptedException {
        NetworkResponse response = null;

        while ((response = finishedResponses.get(requestId)) == null) {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        toSendRequests.remove(response.getHostname());
        finishedResponses.remove(requestId);

        return response;
    }


    class NetworkPollThread extends Thread {

        @Override
        public void run() {
            while (true) {
                tryConnect();
                prepareRequests();
                poll();
            }
        }

        /**
         * Attempt to initiate a connection request from the queued machine
         */
        private void tryConnect() {
            Host host = null;
            SocketChannel channel = null;
            while ((host = waitingConnectHosts.poll()) != null) {
                try {
                    channel = SocketChannel.open();
                    channel.configureBlocking(false);
                    channel.connect(new InetSocketAddress(host.hostname, host.nioPort));
                    channel.register(selector, SelectionKey.OP_CONNECT);
                } catch (Exception e) {
                    e.printStackTrace();
                    connectState.put(host.hostname, DISCONNECTED);
                }
            }
        }

        private void prepareRequests() {
            for (String hostname : waitingRequests.keySet()) {
                ConcurrentLinkedQueue<NetworkRequest> requestQueue = waitingRequests.get(hostname);
                if (!requestQueue.isEmpty() && !toSendRequests.containsKey(hostname)) {
                    NetworkRequest request = requestQueue.poll();
                    toSendRequests.put(hostname, request);

                    SelectionKey key = connections.get(hostname);
                    key.interestOps(SelectionKey.OP_WRITE);
                }
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

                    channel = (SocketChannel) key.channel();

                    if (key.isConnectable()) {
                        finishConnect(key, channel);
                    } else if (key.isWritable()) {
                        sendRequest(key, channel);
                    } else if (key.isReadable()) {
                        readResponse(key, channel);
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

        private void finishConnect(SelectionKey key, SocketChannel channel) throws Exception {

            InetSocketAddress remoteAddress = null;

            try {

                remoteAddress = (InetSocketAddress) channel.getRemoteAddress();

                if (channel.isConnectionPending()) {
                    while (!channel.finishConnect()) {
                        TimeUnit.MILLISECONDS.sleep(100);
                    }
                }

                System.out.println("completed connect with server......");

                // refresh cache
                waitingRequests.put(remoteAddress.getHostName(), new ConcurrentLinkedQueue<>());
                connections.put(remoteAddress.getHostName(), key);
                connectState.put(remoteAddress.getHostName(), CONNECTED);
            } catch (Exception e) {
                e.printStackTrace();
                if (remoteAddress != null) {
                    connectState.put(remoteAddress.getHostName(), DISCONNECTED);
                }
            }
        }

        private void sendRequest(SelectionKey key, SocketChannel channel) throws Exception {
            InetSocketAddress remoteAddress = null;

            try {
                remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
                String hostname = remoteAddress.getHostName();

                NetworkRequest request = toSendRequests.get(hostname);
                ByteBuffer buffer = request.getBuffer();

                channel.write(buffer);
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }

                System.out.println("current request send completed......");
                key.interestOps(SelectionKey.OP_READ);
            } catch (Exception e) {
                e.printStackTrace();

                key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);

                if(remoteAddress != null) {
                    String hostname = remoteAddress.getHostName();

                    NetworkRequest request = toSendRequests.get(hostname);

                    if(request.needResponse()) {
                        NetworkResponse response = new NetworkResponse();
                        response.setHostname(hostname);
                        response.setRequestId(request.getId());
                        response.setError(true);

                        finishedResponses.put(request.getId(), response);
                    } else {
                        toSendRequests.remove(hostname);
                    }
                }
            }

        }

        private void readResponse(SelectionKey key, SocketChannel channel) throws Exception {
            InetSocketAddress remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
            String hostname = remoteAddress.getHostName();

            NetworkRequest request = toSendRequests.get(hostname);
            NetworkResponse response = null;

            if (request.getRequestType().equals(NetworkRequest.REQUEST_SEND_FILE)) {
                response = readSendFileResponse(request.getId(), hostname, channel);
            }

            if (request.needResponse()) {
                finishedResponses.put(request.getId(), response);
            } else {
                toSendRequests.remove(hostname);
            }
        }

        private NetworkResponse readSendFileResponse(String requestId, String hostname, SocketChannel channel) throws Exception {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            channel.read(buffer);
            buffer.flip();

            NetworkResponse response = new NetworkResponse();
            response.setRequestId(requestId);
            response.setHostname(hostname);
            response.setBuffer(buffer);
            response.setError(false);

            return response;
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
