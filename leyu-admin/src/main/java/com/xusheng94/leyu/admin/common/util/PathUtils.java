package com.xusheng94.leyu.admin.common.util;

public final class PathUtils {

    private PathUtils() {}

    /**
     * 拼接并规范化 URL 路径
     * <pre>
     * normalize("sys/user", "page") → "/sys/user/page"
     * normalize("/sys/user/", "/page/") → "/sys/user/page"
     * normalize("", "") → "/"
     * </pre>
     */
    public static String normalize(String basePath, String path) {
        String fullPath = ("/" + nullToEmpty(basePath) + "/" + nullToEmpty(path))
                .replaceAll("/+", "/");

        // 非根路径，去掉末尾 /
        if (fullPath.length() > 1 && fullPath.endsWith("/")) {
            fullPath = fullPath.substring(0, fullPath.length() - 1);
        }

        return fullPath;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
