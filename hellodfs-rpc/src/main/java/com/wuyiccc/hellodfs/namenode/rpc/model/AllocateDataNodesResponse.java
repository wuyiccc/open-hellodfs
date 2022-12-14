// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NameNodeRpcModel.proto

package com.wuyiccc.hellodfs.namenode.rpc.model;

/**
 * Protobuf type {@code com.wuyiccc.hellodfs.namenode.rpc.AllocateDataNodesResponse}
 */
public  final class AllocateDataNodesResponse extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:com.wuyiccc.hellodfs.namenode.rpc.AllocateDataNodesResponse)
    AllocateDataNodesResponseOrBuilder {
  // Use AllocateDataNodesResponse.newBuilder() to construct.
  private AllocateDataNodesResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private AllocateDataNodesResponse() {
    dataNodes_ = "";
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private AllocateDataNodesResponse(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    int mutable_bitField0_ = 0;
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!input.skipField(tag)) {
              done = true;
            }
            break;
          }
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();

            dataNodes_ = s;
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.wuyiccc.hellodfs.namenode.rpc.model.NameNodeRpcModel.internal_static_com_wuyiccc_hellodfs_namenode_rpc_AllocateDataNodesResponse_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.wuyiccc.hellodfs.namenode.rpc.model.NameNodeRpcModel.internal_static_com_wuyiccc_hellodfs_namenode_rpc_AllocateDataNodesResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse.class, com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse.Builder.class);
  }

  public static final int DATANODES_FIELD_NUMBER = 1;
  private volatile java.lang.Object dataNodes_;
  /**
   * <code>optional string dataNodes = 1;</code>
   */
  public java.lang.String getDataNodes() {
    java.lang.Object ref = dataNodes_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      dataNodes_ = s;
      return s;
    }
  }
  /**
   * <code>optional string dataNodes = 1;</code>
   */
  public com.google.protobuf.ByteString
      getDataNodesBytes() {
    java.lang.Object ref = dataNodes_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      dataNodes_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!getDataNodesBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, dataNodes_);
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getDataNodesBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, dataNodes_);
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse)) {
      return super.equals(obj);
    }
    com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse other = (com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse) obj;

    boolean result = true;
    result = result && getDataNodes()
        .equals(other.getDataNodes());
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptorForType().hashCode();
    hash = (37 * hash) + DATANODES_FIELD_NUMBER;
    hash = (53 * hash) + getDataNodes().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code com.wuyiccc.hellodfs.namenode.rpc.AllocateDataNodesResponse}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:com.wuyiccc.hellodfs.namenode.rpc.AllocateDataNodesResponse)
      com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.wuyiccc.hellodfs.namenode.rpc.model.NameNodeRpcModel.internal_static_com_wuyiccc_hellodfs_namenode_rpc_AllocateDataNodesResponse_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.wuyiccc.hellodfs.namenode.rpc.model.NameNodeRpcModel.internal_static_com_wuyiccc_hellodfs_namenode_rpc_AllocateDataNodesResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse.class, com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse.Builder.class);
    }

    // Construct using com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    public Builder clear() {
      super.clear();
      dataNodes_ = "";

      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.wuyiccc.hellodfs.namenode.rpc.model.NameNodeRpcModel.internal_static_com_wuyiccc_hellodfs_namenode_rpc_AllocateDataNodesResponse_descriptor;
    }

    public com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse getDefaultInstanceForType() {
      return com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse.getDefaultInstance();
    }

    public com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse build() {
      com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse buildPartial() {
      com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse result = new com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse(this);
      result.dataNodes_ = dataNodes_;
      onBuilt();
      return result;
    }

    public Builder clone() {
      return (Builder) super.clone();
    }
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.setField(field, value);
    }
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse) {
        return mergeFrom((com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse other) {
      if (other == com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse.getDefaultInstance()) return this;
      if (!other.getDataNodes().isEmpty()) {
        dataNodes_ = other.dataNodes_;
        onChanged();
      }
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private java.lang.Object dataNodes_ = "";
    /**
     * <code>optional string dataNodes = 1;</code>
     */
    public java.lang.String getDataNodes() {
      java.lang.Object ref = dataNodes_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        dataNodes_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <code>optional string dataNodes = 1;</code>
     */
    public com.google.protobuf.ByteString
        getDataNodesBytes() {
      java.lang.Object ref = dataNodes_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        dataNodes_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>optional string dataNodes = 1;</code>
     */
    public Builder setDataNodes(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      dataNodes_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>optional string dataNodes = 1;</code>
     */
    public Builder clearDataNodes() {
      
      dataNodes_ = getDefaultInstance().getDataNodes();
      onChanged();
      return this;
    }
    /**
     * <code>optional string dataNodes = 1;</code>
     */
    public Builder setDataNodesBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      dataNodes_ = value;
      onChanged();
      return this;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }


    // @@protoc_insertion_point(builder_scope:com.wuyiccc.hellodfs.namenode.rpc.AllocateDataNodesResponse)
  }

  // @@protoc_insertion_point(class_scope:com.wuyiccc.hellodfs.namenode.rpc.AllocateDataNodesResponse)
  private static final com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse();
  }

  public static com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<AllocateDataNodesResponse>
      PARSER = new com.google.protobuf.AbstractParser<AllocateDataNodesResponse>() {
    public AllocateDataNodesResponse parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return new AllocateDataNodesResponse(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<AllocateDataNodesResponse> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<AllocateDataNodesResponse> getParserForType() {
    return PARSER;
  }

  public com.wuyiccc.hellodfs.namenode.rpc.model.AllocateDataNodesResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

