package com.wuyiccc.hellodfs.admin.common.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author wuyiccc
 * @date 2021/2/14 11:56
 * 文件工具类
 */
public class FileUtil {

    /**
     * 获取文件的后缀名
     * @param file
     * @return 文件后缀名, 如果没有, 则返回""
     */
    public static String getFileSuffix(MultipartFile file) {
        // 文件名称, 可能会包含路径
        String fileName = file.getOriginalFilename();
        if (StringUtils.isNotBlank(fileName)) {
            String[] fileNameArr = fileName.split("\\.");
            // 获取文件后缀名
            String fileSuffix = fileNameArr[fileNameArr.length - 1];
            return fileSuffix;
        }
        return "";
    }


}
