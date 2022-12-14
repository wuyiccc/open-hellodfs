// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NameNodeRpcServer.proto

package com.wuyiccc.hellodfs.namenode.rpc.service;

public final class NameNodeServer {
  private NameNodeServer() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\027NameNodeRpcServer.proto\022!com.wuyiccc.h" +
      "ellodfs.namenode.rpc\032\026NameNodeRpcModel.p" +
      "roto2\365\r\n\017NameNodeService\022s\n\010register\0222.c" +
      "om.wuyiccc.hellodfs.namenode.rpc.Registe" +
      "rRequest\0323.com.wuyiccc.hellodfs.namenode" +
      ".rpc.RegisterResponse\022v\n\theartBeat\0223.com" +
      ".wuyiccc.hellodfs.namenode.rpc.HeartBeat" +
      "Request\0324.com.wuyiccc.hellodfs.namenode." +
      "rpc.HeartBeatResponse\022j\n\005mkdir\022/.com.wuy" +
      "iccc.hellodfs.namenode.rpc.MkdirRequest\032",
      "0.com.wuyiccc.hellodfs.namenode.rpc.Mkdi" +
      "rResponse\022s\n\010shutdown\0222.com.wuyiccc.hell" +
      "odfs.namenode.rpc.ShutdownRequest\0323.com." +
      "wuyiccc.hellodfs.namenode.rpc.ShutdownRe" +
      "sponse\022\202\001\n\rfetchEditsLog\0227.com.wuyiccc.h" +
      "ellodfs.namenode.rpc.FetchEditsLogReques" +
      "t\0328.com.wuyiccc.hellodfs.namenode.rpc.Fe" +
      "tchEditsLogResponse\022\227\001\n\024updateCheckpoint" +
      "TxId\022>.com.wuyiccc.hellodfs.namenode.rpc" +
      ".UpdateCheckpointTxIdRequest\032?.com.wuyic",
      "cc.hellodfs.namenode.rpc.UpdateCheckpoin" +
      "tTxIdResponse\022u\n\006create\0224.com.wuyiccc.he" +
      "llodfs.namenode.rpc.CreateFileRequest\0325." +
      "com.wuyiccc.hellodfs.namenode.rpc.Create" +
      "FileResponse\022\216\001\n\021allocateDataNodes\022;.com" +
      ".wuyiccc.hellodfs.namenode.rpc.AllocateD" +
      "ataNodesRequest\032<.com.wuyiccc.hellodfs.n" +
      "amenode.rpc.AllocateDataNodesResponse\022\232\001" +
      "\n\025informReplicaReceived\022?.com.wuyiccc.he" +
      "llodfs.namenode.rpc.InformReplicaReceive",
      "dRequest\032@.com.wuyiccc.hellodfs.namenode" +
      ".rpc.InformReplicaReceivedResponse\022\227\001\n\024r" +
      "eportAllStorageInfo\022>.com.wuyiccc.hellod" +
      "fs.namenode.rpc.ReportAllStorageInfoRequ" +
      "est\032?.com.wuyiccc.hellodfs.namenode.rpc." +
      "ReportAllStorageInfoResponse\022\251\001\n\032chooseD" +
      "ataNodeFromReplicas\022D.com.wuyiccc.hellod" +
      "fs.namenode.rpc.ChooseDataNodeFromReplic" +
      "asRequest\032E.com.wuyiccc.hellodfs.namenod" +
      "e.rpc.ChooseDataNodeFromReplicasResponse",
      "\022\221\001\n\022reallocateDataNode\022<.com.wuyiccc.he" +
      "llodfs.namenode.rpc.ReallocateDataNodeRe" +
      "quest\032=.com.wuyiccc.hellodfs.namenode.rp" +
      "c.ReallocateDataNodeResponse\022v\n\trebalanc" +
      "e\0223.com.wuyiccc.hellodfs.namenode.rpc.Re" +
      "balanceRequest\0324.com.wuyiccc.hellodfs.na" +
      "menode.rpc.RebalanceResponseB=\n)com.wuyi" +
      "ccc.hellodfs.namenode.rpc.serviceB\016NameN" +
      "odeServerP\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.wuyiccc.hellodfs.namenode.rpc.model.NameNodeRpcModel.getDescriptor(),
        }, assigner);
    com.wuyiccc.hellodfs.namenode.rpc.model.NameNodeRpcModel.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
