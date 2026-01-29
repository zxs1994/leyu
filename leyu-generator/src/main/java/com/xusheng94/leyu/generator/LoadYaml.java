package com.xusheng94.leyu.generator;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LoadYaml {

    // 业务模块名称，可动态设置
    private static String businessModule = "leyu-admin";

    public static void setBusinessModule(String moduleName) {
        businessModule = moduleName;
    }

    /**
     * 根据当前类的位置定位模块根目录（找到的第一个 pom.xml 所在目录）
     */
    public static String findModuleRoot() {
        try {
            Path classPath = Path.of(LoadYaml.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI()).toAbsolutePath();
            Path dir = classPath;
            while (dir != null) {
                if (Files.exists(dir.resolve("pom.xml"))) {
                    return dir.toString();
                }
                dir = dir.getParent();
            }
        } catch (Exception ignored) {
            // 降级到 user.dir
        }
        return System.getProperty("user.dir");
    }

    /**
     * 向上查找 parent 目录
     * 从当前类位置向上查找，直到找到包含 pom.xml 且拥有 <modules> 的 parent 目录
     */
    public static String findParentDirectory() {
        String moduleRoot = findModuleRoot();
        File current = new File(moduleRoot);

        // 最多向上查找 5 级
        for (int i = 0; i < 5; i++) {
            File pomFile = new File(current, "pom.xml");

            if (pomFile.exists()) {
                try {
                    String content = new String(Files.readAllBytes(pomFile.toPath()));
                    // 如果 pom.xml 包含 <modules>，说明这是 parent 目录
                    if (content.contains("<modules>")) {
                        return current.getAbsolutePath();
                    }
                } catch (Exception e) {
                    // 继续查找
                }
            }

            // 向上一级
            File parent = current.getParentFile();
            if (parent == null) break;
            current = parent;
        }

        // 降级方案：无法找到则返回 user.dir
        return System.getProperty("user.dir");
    }

    // 多模块支持：智能查找配置文件位置
    private static String getConfigPath(String filename) {
        String currentDir = System.getProperty("user.dir");

        // 查找 parent 目录
        String parentPath = findParentDirectory();
        File parentDir = parentPath == null ? null : new File(parentPath);

        // 如果找不到 parent，则尝试使用当前目录的父目录
        if (parentDir == null || !parentDir.exists()) {
            File fallback = new File(currentDir).getParentFile();
            parentDir = (fallback != null && fallback.exists()) ? fallback : null;
        }

        // 自动检测业务模块名（可能是 leyu-admin 或其他名字）
        // 优先查找 */src/main/resources/<filename>
        if (parentDir != null) {
            File[] children = parentDir.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (!child.isDirectory()) continue;
                    if (child.getName().equals("leyu-generator")) continue;
                    File resourcesPath = new File(child, "src/main/resources/" + filename);
                    if (resourcesPath.exists()) {
                        return resourcesPath.getAbsolutePath();
                    }
                }
            }
        }

        // 终极降级：使用配置的业务模块名（保证 parentDir 不为 null）
        if (parentDir == null) {
            throw new RuntimeException("无法定位 parent 目录，且未找到配置文件: " + filename);
        }
        return parentDir.getAbsolutePath() + "/" + businessModule + "/src/main/resources/" + filename;
    }

    private static final String FILE_PATH = getConfigPath("project.yml");
    private static final String DEV_FILE_PATH = getConfigPath("application-dev.yml");

    // 缓存
    private static Map<String, Object> application;
    private static Map<String, Object> applicationDev;

    private static Map<String, Object> loadOnce(String path) {
        Yaml yaml = new Yaml();
        try (InputStream in = Files.newInputStream(Path.of(path))) {
            return yaml.load(in);
        } catch (Exception e) {
            throw new RuntimeException("读取 yml 失败: " + path, e);
        }
    }

    private static Map<String, Object> app() {
        if (application == null) {
            application = loadOnce(FILE_PATH);
        }
        return application;
    }

    private static Map<String, Object> dev() {
        if (applicationDev == null) {
            applicationDev = loadOnce(DEV_FILE_PATH);
        }
        return applicationDev;
    }

    // ========= 对外 API =========

    public static String getBasePackage() {
        return (String) get(app(), "project", "base-package");
    }

    public static String getProperty(String... keys) {
        return (String) get(app(), keys);
    }

    public static String getDevProperty(String... keys) {
        return (String) get(dev(), keys);
    }

    // ========= 核心取值 =========

    @SuppressWarnings("unchecked")
    private static Object get(Map<String, Object> root, String... keys) {
        Object current = root;

        for (String key : keys) {
            if (!(current instanceof Map)) {
                return null;
            }
            current = ((Map<String, Object>) current).get(key);
        }

        return current;
    }

}