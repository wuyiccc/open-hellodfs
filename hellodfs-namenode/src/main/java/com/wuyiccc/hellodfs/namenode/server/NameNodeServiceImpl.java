package com.wuyiccc.hellodfs.namenode.server;

import com.wuyiccc.hellodfs.namenode.rpc.model.*;
import com.wuyiccc.hellodfs.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.stub.StreamObserver;

/**
 * @author wuyiccc
 * @date 2022/5/2 14:22
 */
public class NameNodeServiceImpl implements NameNodeServiceGrpc.NameNodeService {

    public static final Integer STATUS_SUCCESS = 1;

    public static final Integer STATUS_FAILURE = 2;


    private FSNameSystem fsNameSystem;

    private DataNodeManager dataNodeManager;

    public NameNodeServiceImpl(FSNameSystem fsNameSystem, DataNodeManager dataNodeManager) {
        this.fsNameSystem = fsNameSystem;
        this.dataNodeManager = dataNodeManager;
    }


    /**
     * take over datanode register
     */
    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        dataNodeManager.register(request.getIp(), request.getHostname());

        RegisterResponse response = RegisterResponse.newBuilder()
                .setStatus(STATUS_SUCCESS)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void heartBeat(HeartBeatRequest request, StreamObserver<HeartBeatResponse> responseObserver) {
        dataNodeManager.heartBeat(request.getIp(), request.getHostname());

        HeartBeatResponse response = HeartBeatResponse.newBuilder()
                .setStatus(STATUS_SUCCESS)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void mkdir(MkdirRequest request, StreamObserver<MkdirResponse> responseObserver) {
        try {
            this.fsNameSystem.mkdir(request.getPath());

            System.out.println("mkdir: path = " + request.getPath());

            MkdirResponse response = MkdirResponse.newBuilder()
                    .setStatus(STATUS_SUCCESS)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
