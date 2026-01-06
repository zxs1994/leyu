package com.example.template.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 *  读配置文件
 */
public class LoadProperties {
    static final String projectFilePath = "src/main/resources/project.properties";
    static final String devFilePath = "src/main/resources/application-dev.properties";

    public static Properties load(String path) {
        Properties props = new Properties();
        try (Reader reader = Files.newBufferedReader(Path.of(path), StandardCharsets.UTF_8)) {
            props.load(reader); // 使用 Reader 而不是 InputStream
        } catch (IOException e) {
            throw new RuntimeException("加载 properties 文件失败: " + path, e);
        }
        return props;
    }

    public static Properties loadDev () {
        return load(devFilePath);
    }

    public static Properties loadProject () {
        return load(projectFilePath);
    }

    public static String get(String path, String key) {
        return load(path).getProperty(key);
    }

    public static String getBasePackage() {
        return LoadProperties.get(projectFilePath, "project.base-package");
    }

}