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
    public Boolean upload(byte[] file, String filename, long fileSize) throws Exception {

        if (!createFile(filename)) {
            return false;
        }

        System.out.println("success create file in fileDirectory");

        String dataNodeListJson = allocateDataNodeList(filename, fileSize);
        System.out.println(dataNodeListJson);

        JSONArray dataNodeListArray = JSONArray.parseArray(dataNodeListJson);

        System.out.println("apply two datanode: " + dataNodeListArray);

        for (int i = 0; i < dataNodeListArray.size(); i++) {
            JSONObject dataNode = dataNodeListArray.getJSONObject(i);
            String hostname = dataNode.getString("hostname");
            int nioPort = dataNode.getIntValue("nioPort");

            if (!this.nioClient.sendFile(hostname, nioPort, file, filename, fileSize)) {
                dataNode = JSONObject.parseObject(reallocateDataNode(filename, fileSize, hostname));
                hostname = dataNode.getString("hostname");
                nioPort = dataNode.getIntValue("nioPort");
                if (!nioClient.sendFile(hostname, nioPort, file, filename, fileSize)) {
                    throw new Exception("file upload failed...");
                }
            }
        }
        return true;
    }

    private String reallocateDataNode(String filename, long fileSize, String excludeHostname) {
        return null;
    }

    /**
     * download file
     */
    @Override
    public byte[] download(String filename) throws Exception {
        JSONObject datanode = getDataNodeForFile(filename);


        String hostname = datanode.getString("hostname");
        Integer nioPort = datanode.getInteger("nioPort");

        return nioClient.readFile(hostname, nioPort, filename);
    }

    private JSONObject getDataNodeForFile(String filename) throws Exception {
        GetDataNodeForFileRequest request = GetDataNodeForFileRequest.newBuilder().setFilename(filename).build();
        GetDataNodeForFileResponse response = this.nameNode.getDataNodeForFile(request);
        return JSONObject.parseObject(response.getDataNodeInfo());
    }

    private Boolean createFile(String filename) {
        CreateFileRequest request = CreateFileRequest.newBuilder().setFilename(filename).build();
        CreateFileResponse response = this.nameNode.create(request);

        if (response.getStatus() == 1) {
            return true;
        }
        return false;
    }

    private String allocateDataNodeList(String filename, long fileSize) {
        AllocateDataNodesRequest request = AllocateDataNodesRequest.newBuilder().setFilename(filename).setFileSize(fileSize)
                .build();
        AllocateDataNodesResponse response = this.nameNode.allocateDataNodes(request);

        return response.getDataNodes();
    }
}
