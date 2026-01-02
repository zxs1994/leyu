package com.example.template.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class LoadProperties {
    /**
     * 加载指定路径的 properties 文件
     */
    public static Properties load(String path) {
        Properties props = new Properties();
        try {
            byte[] bytes = Files.readAllBytes(Path.of(path));
            props.load(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new RuntimeException("加载 properties 文件失败: " + path, e);
        }
        return props;
    }

    /**
     * 获取指定 properties 文件中的某个 key
     */
    public static String get(String path, String key) {
        return load(path).getProperty(key);
    }

    public static String getBasePackage() {
        return LoadProperties.get("src/main/resources/application.properties", "project.base-package");
    }
}