package com.wuyiccc.hellodfs.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author wuyiccc
 * @date 2022/6/3 17:34
 */
@SpringBootApplication
@MapperScan(basePackages = "com.wuyiccc.hellodfs.admin.mapper")
public class HellodfsAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(HellodfsAdminApplication.class, args);
    }
}
