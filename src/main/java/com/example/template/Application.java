package com.example.template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("${project.base-package}.mapper") // 扫描 Mapper 包
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}