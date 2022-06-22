package com.wuyiccc.hellodfs.admin.controller;

import com.wuyiccc.hellodfs.admin.common.CommonJSONResult;
import com.wuyiccc.hellodfs.admin.common.enumeration.ResponseStatusEnum;
import com.wuyiccc.hellodfs.admin.common.util.FileUtil;
import com.wuyiccc.hellodfs.admin.service.FileUploadService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wuyiccc
 * @date 2021/2/14 11:37
 */
@RestController
@RequestMapping("/hellodfsAdmin/fileUpload")
public class FileUploadController {

    private Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    private FileUploadService fileUploadService;



    /**
     * 用户头像上传没有做登录拦截，对外界开放
     * @param userId
     * @param file 前端传文件时, 可以通过表单来传递, 表单的每一项都会被当做请求参数
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public CommonJSONResult upload(@RequestParam String userId, @RequestParam  MultipartFile file) throws IOException {
        if (file == null) {
            return CommonJSONResult.errorMsg("上传文件不能为空");
        }
        String fileSuffix = FileUtil.getFileSuffix(file);
        // 开始文件上传
        String fileUrlPath = fileUploadService.uploadUserFaceImage(file, userId);
        logger.info("###fileUrlPath:{}", fileUrlPath);
        if (StringUtils.isBlank(fileUrlPath)) {
            CommonJSONResult.errorMsg("文件上传路径为空");
        }
        return CommonJSONResult.ok(fileUrlPath);
    }
}
