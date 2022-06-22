package com.wuyiccc.hellodfs.admin.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.wuyiccc.hellodfs.admin.common.enumeration.FileDirectoryEnum;
import com.wuyiccc.hellodfs.admin.common.util.FileUtil;
import com.wuyiccc.hellodfs.admin.config.AliyunConfig;
import com.wuyiccc.hellodfs.admin.service.FileUploadService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author wuyiccc
 * @date 2021/2/14 11:22
 * 阿里云oss文件上传实现类
 */
@Service
public class FileUploadAliyunOssServiceImpl implements FileUploadService {

    @Autowired
    private AliyunConfig aliyunConfig;

    @Autowired
    private Sid sid;


    @Override
    public String uploadUserFaceImage(MultipartFile file, String userId) throws IOException {
        return uploadSingleFile(file, userId, FileDirectoryEnum.IMAGE_USER_FACE_DIRECTORY.getFileDirectory());
    }

    @Override
    public String uploadUserStorageFile(MultipartFile file, String userId) throws IOException {
        return uploadSingleFile(file, userId, FileDirectoryEnum.IMAGE_USER_STORAGE_DIRECTORY.getFileDirectory());
    }


    /**
     * 将单个文件上传至阿里oss
     * @param file 文件内容
     * @param userId 上传用户的id
     * @param fileDirectory 上传至阿里oss的bucket中对应的目录, 不能以/开头
     * @return 文件在阿里oss上访问的url
     * @throws IOException
     */
    private String uploadSingleFile(MultipartFile file, String userId, String fileDirectory) throws IOException {

        OSS ossClient = new OSSClientBuilder().build(aliyunConfig.getOssFileEndpoint(), aliyunConfig.getAccessKeyId(), aliyunConfig.getAccessKeySecret());
        InputStream inputStream = file.getInputStream();
        String fileName = sid.nextShort();
        // 生成文件全路径名称
        String fileAllDirectoryPathName = fileDirectory + "/" + userId + "/" + fileName + "." + FileUtil.getFileSuffix(file);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 添加objectMetadata可以直接在浏览器上访问图片的URL地址
        //objectMetadata.setContentType("image/jpg");
        ossClient.putObject(aliyunConfig.getOssFileBucketName(), fileAllDirectoryPathName, inputStream, objectMetadata);
        ossClient.shutdown();
        String finalUrlPath = aliyunConfig.getOssFileOssHost() + fileAllDirectoryPathName;
        return finalUrlPath;
    }
}
