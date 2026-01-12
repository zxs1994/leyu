package com.github.zxs1994.java_template;

import com.github.zxs1994.java_template.common.ApiResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
@RequiredArgsConstructor
public class Application {

    private final Environment env;

    @PostConstruct
    public void init() {
        // 注入当前系统版本
        ApiResponse.PROJECT_VERSION = env.getProperty("project.version");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}