// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NameNodeRpcModel.proto

package com.wuyiccc.hellodfs.namenode.rpc.model;

public interface ReportAllStorageInfoRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:com.wuyiccc.hellodfs.namenode.rpc.ReportAllStorageInfoRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>optional string ip = 1;</code>
   */
  java.lang.String getIp();
  /**
   * <code>optional string ip = 1;</code>
   */
  com.google.protobuf.ByteString
      getIpBytes();

  /**
   * <code>optional string hostname = 2;</code>
   */
  java.lang.String getHostname();
  /**
   * <code>optional string hostname = 2;</code>
   */
  com.google.protobuf.ByteString
      getHostnameBytes();

  /**
   * <code>optional string filenameListJson = 3;</code>
   */
  java.lang.String getFilenameListJson();
  /**
   * <code>optional string filenameListJson = 3;</code>
   */
  com.google.protobuf.ByteString
      getFilenameListJsonBytes();

  /**
   * <code>optional int64 storedDataSize = 4;</code>
   */
  long getStoredDataSize();
}
