package com.xusheng94.leyu.common.util;

import com.xusheng94.leyu.entity.SysPermission;
import org.springframework.util.AntPathMatcher;

import java.util.Comparator;
import java.util.List;

public class SysPermissionMatcher {

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 从精确到全局匹配
     */
    public static SysPermission matchExactThenGlobal(
            List<SysPermission> allPermissions,
            String method,
            String path
    ) {

        // 1️⃣ 先过滤 method（支持 *）
        List<SysPermission> methodMatched = allPermissions.stream()
                .filter(p ->
                        "*".equals(p.getMethod())
                                || p.getMethod().equalsIgnoreCase(method)
                )
                .toList();

        // 2️⃣ 精确路径优先（如 /sys/role/{id}）
        SysPermission exact = methodMatched.stream()
                .filter(p -> !p.getPath().endsWith("/**"))
                .filter(p -> antPathMatcher.match(p.getPath(), path))
                .findFirst()
                .orElse(null);


        if (exact != null) {
            System.out.println(exact.toString());
            return exact;
        }

        // 3️⃣ 通配路径（/**、/sys/**），按“路径具体度”排序
        return methodMatched.stream()
                .filter(p -> antPathMatcher.match(p.getPath(), path))
                .sorted(Comparator.comparingInt(SysPermissionMatcher::pathSpecificity).reversed())
                .findFirst()
                .orElse(null);
    }

    /**
     * 路径具体度：
     * 2 - 精确接口（/sys/role/{id}）
     * 1 - 模块级（/sys/role/**）
     * 0 - 全局（/**）
     */
    private static int pathSpecificity(SysPermission p) {
        String path = p.getPath();

        if ("/**".equals(path)) {
            return 0;
        }
        if (path.endsWith("/**")) {
            return 1;
        }
        return 2;
    }

    /**
     * 用于请求鉴权
     */
    public static boolean match(SysPermission rule, String method, String path) {

        // 方法匹配
        if (!"*".equals(rule.getMethod())
                && !rule.getMethod().equalsIgnoreCase(method)) {
            return false;
        }

        // 路径匹配（/** 覆盖一切）
        return antPathMatcher.match(rule.getPath(), path);
    }

    /**
     * rule 是否覆盖 target（仅用于权限模型推导，不用于请求鉴权）
     */
    public static boolean cover(SysPermission rule, SysPermission target) {

        // method 覆盖
        if (!"*".equals(rule.getMethod())
                && !rule.getMethod().equalsIgnoreCase(target.getMethod())) {
            return false;
        }

        // path 覆盖：rule 必须更“宽”
        if (rule.getPath().equals(target.getPath())) {
            return true;
        }

        // 只有 rule 是 /** 或 /xxx/** 这种，才允许覆盖
        if (rule.getPath().endsWith("/**")) {
            return antPathMatcher.match(rule.getPath(), target.getPath());
        }

        return false;
    }
}
