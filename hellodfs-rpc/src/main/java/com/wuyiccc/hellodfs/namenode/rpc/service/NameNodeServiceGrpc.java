package com.wuyiccc.hellodfs.namenode.rpc.service;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;

@javax.annotation.Generated("by gRPC proto compiler")
public class NameNodeServiceGrpc {

  private NameNodeServiceGrpc() {}

  public static final String SERVICE_NAME = "com.wuyiccc.hellodfs.namenode.rpc.NameNodeService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.wuyiccc.hellodfs.namenode.rpc.model.RegisterRequest,
      com.wuyiccc.hellodfs.namenode.rpc.model.RegisterResponse> METHOD_REGISTER =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "com.wuyiccc.hellodfs.namenode.rpc.NameNodeService", "register"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.RegisterRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.RegisterResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatRequest,
      com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatResponse> METHOD_HEART_BEAT =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "com.wuyiccc.hellodfs.namenode.rpc.NameNodeService", "heartBeat"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.wuyiccc.hellodfs.namenode.rpc.model.MkdirRequest,
      com.wuyiccc.hellodfs.namenode.rpc.model.MkdirResponse> METHOD_MKDIR =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "com.wuyiccc.hellodfs.namenode.rpc.NameNodeService", "mkdir"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.MkdirRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.MkdirResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownRequest,
      com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownResponse> METHOD_SHUTDOWN =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "com.wuyiccc.hellodfs.namenode.rpc.NameNodeService", "shutdown"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogRequest,
      com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogResponse> METHOD_FETCH_EDITS_LOG =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "com.wuyiccc.hellodfs.namenode.rpc.NameNodeService", "fetchEditsLog"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdRequest,
      com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdResponse> METHOD_UPDATE_CHECKPOINT_TX_ID =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "com.wuyiccc.hellodfs.namenode.rpc.NameNodeService", "updateCheckpointTxId"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileRequest,
      com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileResponse> METHOD_CREATE =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "com.wuyiccc.hellodfs.namenode.rpc.NameNodeService", "create"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesRequest,
      com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse> METHOD_ALLOCATE_DATA_NODES =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "com.wuyiccc.hellodfs.namenode.rpc.NameNodeService", "allocateDataNodes"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedRequest,
      com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedResponse> METHOD_INFORM_REPLICA_RECEIVED =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "com.wuyiccc.hellodfs.namenode.rpc.NameNodeService", "informReplicaReceived"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoRequest,
      com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoResponse> METHOD_REPORT_ALL_STORAGE_INFO =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "com.wuyiccc.hellodfs.namenode.rpc.NameNodeService", "reportAllStorageInfo"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasRequest,
      com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasResponse> METHOD_CHOOSE_DATA_NODE_FROM_REPLICAS =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "com.wuyiccc.hellodfs.namenode.rpc.NameNodeService", "chooseDataNodeFromReplicas"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeRequest,
      com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeResponse> METHOD_REALLOCATE_DATA_NODE =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "com.wuyiccc.hellodfs.namenode.rpc.NameNodeService", "reallocateDataNode"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi
  public static final io.grpc.MethodDescriptor<com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceRequest,
      com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceResponse> METHOD_REBALANCE =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "com.wuyiccc.hellodfs.namenode.rpc.NameNodeService", "rebalance"),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceResponse.getDefaultInstance()));

  public static NameNodeServiceStub newStub(io.grpc.Channel channel) {
    return new NameNodeServiceStub(channel);
  }

  public static NameNodeServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new NameNodeServiceBlockingStub(channel);
  }

  public static NameNodeServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new NameNodeServiceFutureStub(channel);
  }

  public static interface NameNodeService {

    public void register(com.wuyiccc.hellodfs.namenode.rpc.model.RegisterRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.RegisterResponse> responseObserver);

    public void heartBeat(com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatResponse> responseObserver);

    public void mkdir(com.wuyiccc.hellodfs.namenode.rpc.model.MkdirRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.MkdirResponse> responseObserver);

    public void shutdown(com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownResponse> responseObserver);

    public void fetchEditsLog(com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogResponse> responseObserver);

    public void updateCheckpointTxId(com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdResponse> responseObserver);

    public void create(com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileResponse> responseObserver);

    public void allocateDataNodes(com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse> responseObserver);

    public void informReplicaReceived(com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedResponse> responseObserver);

    public void reportAllStorageInfo(com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoResponse> responseObserver);

    public void chooseDataNodeFromReplicas(com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasResponse> responseObserver);

    public void reallocateDataNode(com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeResponse> responseObserver);

    public void rebalance(com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceResponse> responseObserver);
  }

  public static interface NameNodeServiceBlockingClient {

    public com.wuyiccc.hellodfs.namenode.rpc.model.RegisterResponse register(com.wuyiccc.hellodfs.namenode.rpc.model.RegisterRequest request);

    public com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatResponse heartBeat(com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatRequest request);

    public com.wuyiccc.hellodfs.namenode.rpc.model.MkdirResponse mkdir(com.wuyiccc.hellodfs.namenode.rpc.model.MkdirRequest request);

    public com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownResponse shutdown(com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownRequest request);

    public com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogResponse fetchEditsLog(com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogRequest request);

    public com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdResponse updateCheckpointTxId(com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdRequest request);

    public com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileResponse create(com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileRequest request);

    public com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse allocateDataNodes(com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesRequest request);

    public com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedResponse informReplicaReceived(com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedRequest request);

    public com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoResponse reportAllStorageInfo(com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoRequest request);

    public com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasResponse chooseDataNodeFromReplicas(com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasRequest request);

    public com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeResponse reallocateDataNode(com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeRequest request);

    public com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceResponse rebalance(com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceRequest request);
  }

  public static interface NameNodeServiceFutureClient {

    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.RegisterResponse> register(
        com.wuyiccc.hellodfs.namenode.rpc.model.RegisterRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatResponse> heartBeat(
        com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.MkdirResponse> mkdir(
        com.wuyiccc.hellodfs.namenode.rpc.model.MkdirRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownResponse> shutdown(
        com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogResponse> fetchEditsLog(
        com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdResponse> updateCheckpointTxId(
        com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileResponse> create(
        com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse> allocateDataNodes(
        com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedResponse> informReplicaReceived(
        com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoResponse> reportAllStorageInfo(
        com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasResponse> chooseDataNodeFromReplicas(
        com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeResponse> reallocateDataNode(
        com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeRequest request);

    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceResponse> rebalance(
        com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceRequest request);
  }

  public static class NameNodeServiceStub extends io.grpc.stub.AbstractStub<NameNodeServiceStub>
      implements NameNodeService {
    private NameNodeServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NameNodeServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NameNodeServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NameNodeServiceStub(channel, callOptions);
    }

    @java.lang.Override
    public void register(com.wuyiccc.hellodfs.namenode.rpc.model.RegisterRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.RegisterResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_REGISTER, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void heartBeat(com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_HEART_BEAT, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void mkdir(com.wuyiccc.hellodfs.namenode.rpc.model.MkdirRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.MkdirResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_MKDIR, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void shutdown(com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SHUTDOWN, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void fetchEditsLog(com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_FETCH_EDITS_LOG, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void updateCheckpointTxId(com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_UPDATE_CHECKPOINT_TX_ID, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void create(com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CREATE, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void allocateDataNodes(com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_ALLOCATE_DATA_NODES, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void informReplicaReceived(com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_INFORM_REPLICA_RECEIVED, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void reportAllStorageInfo(com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_REPORT_ALL_STORAGE_INFO, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void chooseDataNodeFromReplicas(com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_CHOOSE_DATA_NODE_FROM_REPLICAS, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void reallocateDataNode(com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_REALLOCATE_DATA_NODE, getCallOptions()), request, responseObserver);
    }

    @java.lang.Override
    public void rebalance(com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceRequest request,
        io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_REBALANCE, getCallOptions()), request, responseObserver);
    }
  }

  public static class NameNodeServiceBlockingStub extends io.grpc.stub.AbstractStub<NameNodeServiceBlockingStub>
      implements NameNodeServiceBlockingClient {
    private NameNodeServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NameNodeServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NameNodeServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NameNodeServiceBlockingStub(channel, callOptions);
    }

    @java.lang.Override
    public com.wuyiccc.hellodfs.namenode.rpc.model.RegisterResponse register(com.wuyiccc.hellodfs.namenode.rpc.model.RegisterRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_REGISTER, getCallOptions(), request);
    }

    @java.lang.Override
    public com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatResponse heartBeat(com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_HEART_BEAT, getCallOptions(), request);
    }

    @java.lang.Override
    public com.wuyiccc.hellodfs.namenode.rpc.model.MkdirResponse mkdir(com.wuyiccc.hellodfs.namenode.rpc.model.MkdirRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_MKDIR, getCallOptions(), request);
    }

    @java.lang.Override
    public com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownResponse shutdown(com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SHUTDOWN, getCallOptions(), request);
    }

    @java.lang.Override
    public com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogResponse fetchEditsLog(com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_FETCH_EDITS_LOG, getCallOptions(), request);
    }

    @java.lang.Override
    public com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdResponse updateCheckpointTxId(com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_UPDATE_CHECKPOINT_TX_ID, getCallOptions(), request);
    }

    @java.lang.Override
    public com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileResponse create(com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CREATE, getCallOptions(), request);
    }

    @java.lang.Override
    public com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse allocateDataNodes(com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_ALLOCATE_DATA_NODES, getCallOptions(), request);
    }

    @java.lang.Override
    public com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedResponse informReplicaReceived(com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_INFORM_REPLICA_RECEIVED, getCallOptions(), request);
    }

    @java.lang.Override
    public com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoResponse reportAllStorageInfo(com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_REPORT_ALL_STORAGE_INFO, getCallOptions(), request);
    }

    @java.lang.Override
    public com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasResponse chooseDataNodeFromReplicas(com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_CHOOSE_DATA_NODE_FROM_REPLICAS, getCallOptions(), request);
    }

    @java.lang.Override
    public com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeResponse reallocateDataNode(com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_REALLOCATE_DATA_NODE, getCallOptions(), request);
    }

    @java.lang.Override
    public com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceResponse rebalance(com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_REBALANCE, getCallOptions(), request);
    }
  }

  public static class NameNodeServiceFutureStub extends io.grpc.stub.AbstractStub<NameNodeServiceFutureStub>
      implements NameNodeServiceFutureClient {
    private NameNodeServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NameNodeServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected NameNodeServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NameNodeServiceFutureStub(channel, callOptions);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.RegisterResponse> register(
        com.wuyiccc.hellodfs.namenode.rpc.model.RegisterRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_REGISTER, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatResponse> heartBeat(
        com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_HEART_BEAT, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.MkdirResponse> mkdir(
        com.wuyiccc.hellodfs.namenode.rpc.model.MkdirRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_MKDIR, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownResponse> shutdown(
        com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SHUTDOWN, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogResponse> fetchEditsLog(
        com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_FETCH_EDITS_LOG, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdResponse> updateCheckpointTxId(
        com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_UPDATE_CHECKPOINT_TX_ID, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileResponse> create(
        com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CREATE, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse> allocateDataNodes(
        com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_ALLOCATE_DATA_NODES, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedResponse> informReplicaReceived(
        com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_INFORM_REPLICA_RECEIVED, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoResponse> reportAllStorageInfo(
        com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_REPORT_ALL_STORAGE_INFO, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasResponse> chooseDataNodeFromReplicas(
        com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_CHOOSE_DATA_NODE_FROM_REPLICAS, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeResponse> reallocateDataNode(
        com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_REALLOCATE_DATA_NODE, getCallOptions()), request);
    }

    @java.lang.Override
    public com.google.common.util.concurrent.ListenableFuture<com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceResponse> rebalance(
        com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_REBALANCE, getCallOptions()), request);
    }
  }

  private static final int METHODID_REGISTER = 0;
  private static final int METHODID_HEART_BEAT = 1;
  private static final int METHODID_MKDIR = 2;
  private static final int METHODID_SHUTDOWN = 3;
  private static final int METHODID_FETCH_EDITS_LOG = 4;
  private static final int METHODID_UPDATE_CHECKPOINT_TX_ID = 5;
  private static final int METHODID_CREATE = 6;
  private static final int METHODID_ALLOCATE_DATA_NODES = 7;
  private static final int METHODID_INFORM_REPLICA_RECEIVED = 8;
  private static final int METHODID_REPORT_ALL_STORAGE_INFO = 9;
  private static final int METHODID_CHOOSE_DATA_NODE_FROM_REPLICAS = 10;
  private static final int METHODID_REALLOCATE_DATA_NODE = 11;
  private static final int METHODID_REBALANCE = 12;

  private static class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final NameNodeService serviceImpl;
    private final int methodId;

    public MethodHandlers(NameNodeService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REGISTER:
          serviceImpl.register((com.wuyiccc.hellodfs.namenode.rpc.model.RegisterRequest) request,
              (io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.RegisterResponse>) responseObserver);
          break;
        case METHODID_HEART_BEAT:
          serviceImpl.heartBeat((com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatRequest) request,
              (io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatResponse>) responseObserver);
          break;
        case METHODID_MKDIR:
          serviceImpl.mkdir((com.wuyiccc.hellodfs.namenode.rpc.model.MkdirRequest) request,
              (io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.MkdirResponse>) responseObserver);
          break;
        case METHODID_SHUTDOWN:
          serviceImpl.shutdown((com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownRequest) request,
              (io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownResponse>) responseObserver);
          break;
        case METHODID_FETCH_EDITS_LOG:
          serviceImpl.fetchEditsLog((com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogRequest) request,
              (io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogResponse>) responseObserver);
          break;
        case METHODID_UPDATE_CHECKPOINT_TX_ID:
          serviceImpl.updateCheckpointTxId((com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdRequest) request,
              (io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdResponse>) responseObserver);
          break;
        case METHODID_CREATE:
          serviceImpl.create((com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileRequest) request,
              (io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileResponse>) responseObserver);
          break;
        case METHODID_ALLOCATE_DATA_NODES:
          serviceImpl.allocateDataNodes((com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesRequest) request,
              (io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse>) responseObserver);
          break;
        case METHODID_INFORM_REPLICA_RECEIVED:
          serviceImpl.informReplicaReceived((com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedRequest) request,
              (io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedResponse>) responseObserver);
          break;
        case METHODID_REPORT_ALL_STORAGE_INFO:
          serviceImpl.reportAllStorageInfo((com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoRequest) request,
              (io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoResponse>) responseObserver);
          break;
        case METHODID_CHOOSE_DATA_NODE_FROM_REPLICAS:
          serviceImpl.chooseDataNodeFromReplicas((com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasRequest) request,
              (io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasResponse>) responseObserver);
          break;
        case METHODID_REALLOCATE_DATA_NODE:
          serviceImpl.reallocateDataNode((com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeRequest) request,
              (io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeResponse>) responseObserver);
          break;
        case METHODID_REBALANCE:
          serviceImpl.rebalance((com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceRequest) request,
              (io.grpc.stub.StreamObserver<com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static io.grpc.ServerServiceDefinition bindService(
      final NameNodeService serviceImpl) {
    return io.grpc.ServerServiceDefinition.builder(SERVICE_NAME)
        .addMethod(
          METHOD_REGISTER,
          asyncUnaryCall(
            new MethodHandlers<
              com.wuyiccc.hellodfs.namenode.rpc.model.RegisterRequest,
              com.wuyiccc.hellodfs.namenode.rpc.model.RegisterResponse>(
                serviceImpl, METHODID_REGISTER)))
        .addMethod(
          METHOD_HEART_BEAT,
          asyncUnaryCall(
            new MethodHandlers<
              com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatRequest,
              com.wuyiccc.hellodfs.namenode.rpc.model.HeartBeatResponse>(
                serviceImpl, METHODID_HEART_BEAT)))
        .addMethod(
          METHOD_MKDIR,
          asyncUnaryCall(
            new MethodHandlers<
              com.wuyiccc.hellodfs.namenode.rpc.model.MkdirRequest,
              com.wuyiccc.hellodfs.namenode.rpc.model.MkdirResponse>(
                serviceImpl, METHODID_MKDIR)))
        .addMethod(
          METHOD_SHUTDOWN,
          asyncUnaryCall(
            new MethodHandlers<
              com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownRequest,
              com.wuyiccc.hellodfs.namenode.rpc.model.ShutdownResponse>(
                serviceImpl, METHODID_SHUTDOWN)))
        .addMethod(
          METHOD_FETCH_EDITS_LOG,
          asyncUnaryCall(
            new MethodHandlers<
              com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogRequest,
              com.wuyiccc.hellodfs.namenode.rpc.model.FetchEditsLogResponse>(
                serviceImpl, METHODID_FETCH_EDITS_LOG)))
        .addMethod(
          METHOD_UPDATE_CHECKPOINT_TX_ID,
          asyncUnaryCall(
            new MethodHandlers<
              com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdRequest,
              com.wuyiccc.hellodfs.namenode.rpc.model.UpdateCheckpointTxIdResponse>(
                serviceImpl, METHODID_UPDATE_CHECKPOINT_TX_ID)))
        .addMethod(
          METHOD_CREATE,
          asyncUnaryCall(
            new MethodHandlers<
              com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileRequest,
              com.wuyiccc.hellodfs.namenode.rpc.model.CreateFileResponse>(
                serviceImpl, METHODID_CREATE)))
        .addMethod(
          METHOD_ALLOCATE_DATA_NODES,
          asyncUnaryCall(
            new MethodHandlers<
              com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesRequest,
              com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse>(
                serviceImpl, METHODID_ALLOCATE_DATA_NODES)))
        .addMethod(
          METHOD_INFORM_REPLICA_RECEIVED,
          asyncUnaryCall(
            new MethodHandlers<
              com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedRequest,
              com.wuyiccc.hellodfs.namenode.rpc.model.InformReplicaReceivedResponse>(
                serviceImpl, METHODID_INFORM_REPLICA_RECEIVED)))
        .addMethod(
          METHOD_REPORT_ALL_STORAGE_INFO,
          asyncUnaryCall(
            new MethodHandlers<
              com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoRequest,
              com.wuyiccc.hellodfs.namenode.rpc.model.ReportAllStorageInfoResponse>(
                serviceImpl, METHODID_REPORT_ALL_STORAGE_INFO)))
        .addMethod(
          METHOD_CHOOSE_DATA_NODE_FROM_REPLICAS,
          asyncUnaryCall(
            new MethodHandlers<
              com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasRequest,
              com.wuyiccc.hellodfs.namenode.rpc.model.ChooseDataNodeFromReplicasResponse>(
                serviceImpl, METHODID_CHOOSE_DATA_NODE_FROM_REPLICAS)))
        .addMethod(
          METHOD_REALLOCATE_DATA_NODE,
          asyncUnaryCall(
            new MethodHandlers<
              com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeRequest,
              com.wuyiccc.hellodfs.namenode.rpc.model.ReallocateDataNodeResponse>(
                serviceImpl, METHODID_REALLOCATE_DATA_NODE)))
        .addMethod(
          METHOD_REBALANCE,
          asyncUnaryCall(
            new MethodHandlers<
              com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceRequest,
              com.wuyiccc.hellodfs.namenode.rpc.model.RebalanceResponse>(
                serviceImpl, METHODID_REBALANCE)))
        .build();
  }
}
