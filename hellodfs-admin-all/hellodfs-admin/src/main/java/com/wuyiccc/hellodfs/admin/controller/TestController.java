package com.wuyiccc.hellodfs.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuyiccc
 * @date 2022/6/3 18:13
 */
@RestController
@RequestMapping("/hellodfsAdmin/test")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/hello")
    public Object hello() {
        log.info("hello, im hellodfs-admin service");
        return "hello";

    }
}
