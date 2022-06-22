package com.wuyiccc.hellodfs.admin.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author wuyiccc
 * @date 2021/2/14 11:21
 */
public interface FileUploadService {


    /**
     * 上传用户的头像图片
     * @param file 文件
     * @param userId 用户id
     * @return 头像图片的访问url
     * @throws IOException
     */
    String uploadUserFaceImage(MultipartFile file, String userId) throws IOException;

    /**
     * 上传用户的存储文件
     * @param file
     * @param userId
     * @return 文章图片的访问url
     * @throws IOException
     */
    String uploadUserStorageFile(MultipartFile file, String userId) throws IOException;
}
