package devtools;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LoadYaml {

    private static final String FILE_PATH = "src/main/resources/project.yml";
    private static final String DEV_FILE_PATH = "src/main/resources/application-dev.yml";

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