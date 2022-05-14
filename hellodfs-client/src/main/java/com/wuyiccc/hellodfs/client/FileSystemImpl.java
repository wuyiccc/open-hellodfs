package com.wuyiccc.hellodfs.client;

import com.wuyiccc.hellodfs.namenode.rpc.model.MkdirRequest;
import com.wuyiccc.hellodfs.namenode.rpc.model.MkdirResponse;
import com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownRequest;
import com.wuyiccc.hellodfs.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;

/**
 * @author wuyiccc
 * @date 2022/5/3 9:23
 */
public class FileSystemImpl implements FileSystem {

    private NameNodeServiceGrpc.NameNodeServiceBlockingStub nameNode;

    private static final String NAME_NODE_HOSTNAME = "localhost";
    private static final Integer NAME_NODE_PORT = 50070;

    public FileSystemImpl() {

        ManagedChannel channel = NettyChannelBuilder.forAddress(NAME_NODE_HOSTNAME, NAME_NODE_PORT).negotiationType(NegotiationType.PLAINTEXT).build();
        this.nameNode = NameNodeServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public void mkdir(String path) throws Exception {
        MkdirRequest request = MkdirRequest.newBuilder()
                .setPath(path)
                .build();
        MkdirResponse response = this.nameNode.mkdir(request);

        System.out.println("mkdir response: " + response.getStatus());
    }

    @Override
    public void shutdown() throws Exception {

        ShutdownRequest request = ShutdownRequest.newBuilder()
                .setCode(1)
                .build();
        this.nameNode.shutdown(request);
    }

    @Override
    public void upload(byte[] file, String filename) throws Exception {

    }
}
