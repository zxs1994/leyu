package com.xusheng94.leyu;

import com.xusheng94.leyu.common.cache.SysPermissionCache;
import com.xusheng94.leyu.common.ApiResponse;
import com.xusheng94.leyu.config.security.SysPermissionScanner;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
@RequiredArgsConstructor
public class Application {

    private final Environment env;
    private final SysPermissionScanner sysPermissionScanner;
    private final SysPermissionCache sysPermissionCache;

    @PostConstruct
    public void init() {

        // 注入当前系统版本
        ApiResponse.PROJECT_VERSION = env.getProperty("project.version");
        // 扫描入库权限
        sysPermissionScanner.scanAndSave();
        // 权限缓存初始化
        sysPermissionCache.reload();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}