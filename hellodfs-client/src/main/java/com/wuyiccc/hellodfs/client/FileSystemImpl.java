package com.wuyiccc.hellodfs.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuyiccc.hellodfs.namenode.rpc.model.*;
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

    private NIOClient nioClient;

    public FileSystemImpl() {

        ManagedChannel channel = NettyChannelBuilder.forAddress(NAME_NODE_HOSTNAME, NAME_NODE_PORT).negotiationType(NegotiationType.PLAINTEXT).build();
        this.nameNode = NameNodeServiceGrpc.newBlockingStub(channel);
        this.nioClient = new NIOClient();
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
    public Boolean upload(FileInfo fileInfo, ResponseCallback callback) throws Exception {

        if (!createFile(fileInfo.getFilename())) {
            return false;
        }

        System.out.println("success create file in fileDirectory");

        JSONArray dataNodeListArray = allocateDataNodeList(fileInfo.getFilename(), fileInfo.getFileLength());


        System.out.println("apply two datanode: " + dataNodeListArray);

        for (int i = 0; i < dataNodeListArray.size(); i++) {
            Host host = getHost(dataNodeListArray.getJSONObject(i));

            if (!nioClient.sendFile(fileInfo, host, callback)) {
                host = reallocateDataNode(fileInfo, host.getId());
                nioClient.sendFile(fileInfo, host, null);
            }
        }
        return true;
    }

    @Override
    public Boolean retryUpload(FileInfo fileInfo, Host excludedHost) throws Exception {
        Host host = reallocateDataNode(fileInfo, excludedHost.getId());
        nioClient.sendFile(fileInfo, host, null);
        return true;
    }


    private Host getHost(JSONObject dataNode) {
        Host host = new Host();
        host.setHostname(dataNode.getString("hostname"));
        host.setIp(dataNode.getString("ip"));
        host.setNioPort(dataNode.getInteger("nioPort"));
        return host;
    }

    /**
     * download file
     */
    @Override
    public byte[] download(String filename) throws Exception {
        Host datanode = chooseDataNodeFromReplicas(filename, "");

        byte[] file = null;

        try {
            file = nioClient.readFile(datanode, filename, true);
        } catch (Exception e) {
            datanode = chooseDataNodeFromReplicas(filename, datanode.getId());
            try {
                file = nioClient.readFile(datanode, filename, false);
            } catch (Exception e2) {
                throw e2;
            }
        }

        return file;
    }

    private Host chooseDataNodeFromReplicas(String filename, String excludedDataNodeId) throws Exception {
        ChooseDataNodeFromReplicasRequest request = ChooseDataNodeFromReplicasRequest
                .newBuilder()
                .setFilename(filename)
                .setExcludedDataNodeId(excludedDataNodeId)
                .build();
        ChooseDataNodeFromReplicasResponse response = this.nameNode.chooseDataNodeFromReplicas(request);
        return getHost(JSONObject.parseObject(response.getDataNodeInfo()));
    }

    private Boolean createFile(String filename) {
        CreateFileRequest request = CreateFileRequest.newBuilder().setFilename(filename).build();
        CreateFileResponse response = this.nameNode.create(request);

        if (response.getStatus() == 1) {
            return true;
        }
        return false;
    }

    public JSONArray allocateDataNodeList(String filename, long fileSize) {
        AllocateDataNodesRequest request = AllocateDataNodesRequest.newBuilder().setFilename(filename).setFileSize(fileSize).build();
        AllocateDataNodesResponse response = this.nameNode.allocateDataNodes(request);

        return JSONArray.parseArray(response.getDataNodes());
    }


    public Host reallocateDataNode(FileInfo fileInfo, String excludedHostId) {
        ReallocateDataNodeRequest request = ReallocateDataNodeRequest.newBuilder()
                .setFilename(fileInfo.getFilename())
                .setFileSize(fileInfo.getFileLength())
                .setExcludedDataNodeId(excludedHostId)
                .build();
        ReallocateDataNodeResponse response = this.nameNode.reallocateDataNode(request);
        return getHost(JSONObject.parseObject(response.getDataNodeInfo()));
    }
}
