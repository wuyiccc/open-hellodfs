package com.wuyiccc.hellodfs.admin.config;

import com.wuyiccc.hellodfs.admin.interceptor.UserLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author wuyiccc
 * @date 2020/10/24 10:32
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public UserLoginInterceptor userLoginInterceptor() {
        return new UserLoginInterceptor();
    }


    /**
     * 注册拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userLoginInterceptor())
                .addPathPatterns("/hellodfsAdmin/user/logout")
        ;
    }
}
