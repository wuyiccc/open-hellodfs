package com.wuyiccc.hellodfs.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @author wuyiccc
 * @date 2021/1/7 17:19
 * 岂曰无衣，与子同袍~
 *
 * knife4j接口文档配置
 * https://doc.xiaominfo.com/knife4j/action/springboot.html
 * 生成的文档地址：http://localhost:9000/doc.html#/home
 */

@Configuration
@EnableSwagger2WebMvc
public class Knife4jConfiguration {


    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        /**
         * DocumentationType.SWAGGER_2: 选择文档类型为Swagger2
         * apiInfo对象：
         *      description: swagger-ui模块界面的主页展示部分的简介信息
         *      termsOfServiceUrl：swagger-ui模块界面的主页展示部分的服务Url信息
         *      contact: swagger-ui模块界面的主页展示部分的作者
         *      version: swagger-ui模块界面的主页展示部分的版本信息
         * groupName: swagger-ui模块在下拉框的模块名称
         * select: 选中定义的apiInfo信息供select使用
         * apis(RequestHandlerSelectors.basePackage("")): 项目接口所在的包路径对象
         * paths(PathSelectors.any()): 将我们项目中所有接口的请求路径都暴露给swagger-ui
         */
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .description("hellodfs项目的接口文档")
                        .termsOfServiceUrl("http://localhost:9000/")
                        .contact("wuyiccc")
                        .version("beta-1.0")
                        .build())
                .groupName("hellodfs服务端")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.wuyiccc.hellodfs.admin.controller"))
                .paths(PathSelectors.any())
                .build();
    }
}
