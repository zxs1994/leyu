package com.xusheng94.leyu.admin.config;

import com.xusheng94.leyu.admin.config.myBatisPlus.IgnoreDataPermissionInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final IgnoreDataPermissionInterceptor ignoreDataPermissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ignoreDataPermissionInterceptor);
    }
}