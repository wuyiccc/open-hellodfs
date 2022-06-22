package com.wuyiccc.hellodfs.admin.controller;

import com.wuyiccc.hellodfs.admin.common.CommonJSONResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuyiccc
 * @date 2022/6/5 10:28
 */
@RequestMapping("/hellodfsAdmin/file")
@RestController
public class FileController {

    private Logger logger = LoggerFactory.getLogger(FileController.class);

    @PostMapping("/upload")
    public CommonJSONResult upload() {
        //1. 检查目录id是否存在


        // 2. 判断用户网盘存量是否不足

        // 3. 上传到oss

        // 4. 写入到数据表

        // 5. 更新用户网盘的使用存量

        return null;
    }

}
