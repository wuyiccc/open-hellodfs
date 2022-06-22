package com.wuyiccc.hellodfs.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author wuyiccc
 * @date 2021/2/10 11:42
 */
@Component
@PropertySource("classpath:aliyun.properties")
public class AliyunConfig {


    @Value("${aliyun.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.oss.file.endpoint}")
    private String ossFileEndpoint;

    @Value("${aliyun.oss.file.bucketName}")
    private String ossFileBucketName;


    @Value("${aliyun.oss.file.ossHost}")
    private String ossFileOssHost;


    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public String getOssFileEndpoint() {
        return ossFileEndpoint;
    }

    public String getOssFileBucketName() {
        return ossFileBucketName;
    }

    public String getOssFileOssHost() {
        return ossFileOssHost;
    }
}
