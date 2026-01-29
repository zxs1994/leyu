package com.xusheng94.leyu.admin.config.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.xusheng94.leyu.admin.config.AuthLevelResolver;
import com.xusheng94.leyu.admin.entity.SysPermission;
import com.xusheng94.leyu.admin.common.enums.AuthLevel;
import com.xusheng94.leyu.admin.mapper.SysPermissionMapper;
import com.xusheng94.leyu.admin.common.util.PathUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * 系统权限扫描器（幂等入库）
 *
 * <p>功能：</p>
 * <ul>
 *   <li>启动时扫描所有 @RestController</li>
 *   <li>解析 RequestMapping / Operation</li>
 *   <li>自动生成权限数据</li>
 * </ul>
 *
 * <p>特性：</p>
 * <ul>
 *   <li>支持重复执行（幂等）</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class SysPermissionScanner {

    @Value("${sys-permission.scan-on-startup:false}")
    private boolean scanOnStartup;

    private final ApplicationContext applicationContext;
    private final SysPermissionMapper sysPermissionMapper;
    private final AuthLevelResolver authLevelResolver;

    public void scanAndSave() {

        if (!scanOnStartup) return;

        System.out.println("=== SysPermissionScanner：开始扫描权限 ===");

        // 扫描前统一标记为已删除
        sysPermissionMapper.update(
                null,
                new UpdateWrapper<SysPermission>().set("status", 0)
        );

        // 初始化权限, 只会插入一次
        initGlobalPermissions();

        Map<String, Object> controllers =
                applicationContext.getBeansWithAnnotation(RestController.class);

        for (Object controllerBean : controllers.values()) {

            Class<?> clazz = AopUtils.getTargetClass(controllerBean);

            // 排除 swagger
            String className = clazz.getName().toLowerCase();
            if (className.contains("swagger") || className.contains("openapi")) {
                continue;
            }

            // 类级别 如"/sys/role"
            String basePath = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping rm = clazz.getAnnotation(RequestMapping.class);
                if (rm.value().length > 0) basePath = rm.value()[0];
            }

            // 标签名 如"系统--角色"
            String TagName = "";
            if (clazz.isAnnotationPresent(Tag.class)) {
                Tag tag = clazz.getAnnotation(Tag.class);
                if (!tag.name().isEmpty()) TagName = tag.name();
            }

            String[] paths = resolveModuleHierarchy(basePath, "/");
            if (paths.length == 0) continue;
            String[] moduleNames = resolveModuleHierarchy(TagName, "--");

            StringBuilder pathBuilder = new StringBuilder();
            StringBuilder codeBuilder = new StringBuilder();

            Long parentId = 1L; // 根模块

            for (int i = 0; i < paths.length; i++) {

                String segment = paths[i];

                String moduleName =
                        moduleNames.length > i
                                ? moduleNames[i]
                                : segment; // fallback

                // /sys → /sys/user → /sys/user/role
                pathBuilder.append("/").append(segment);

                // sys → sys:user → sys:user:role
                if (!codeBuilder.isEmpty()) {
                    codeBuilder.append(":");
                }
                codeBuilder.append(segment);

                parentId = initModulePermission(
                        codeBuilder.toString(),
                        moduleName,
                        PathUtils.normalize(pathBuilder.toString(), "**"),
                        parentId
                );
            }

            // 2️⃣ 接口级权限
            for (Method method : clazz.getDeclaredMethods()) {

                MappingInfo mapping = resolveMapping(method);

                if (mapping == null) continue;

                for (String path : mapping.paths) {

                    String fullPath = PathUtils.normalize(basePath, path);
                    String module = String.join(":", paths);
//                    System.out.println(module + " " + method.getName() + " " + fullPath);
                    String code = module + ":" + method.getName();

                    String name = code;
                    if (method.isAnnotationPresent(Operation.class)) {
                        Operation op = method.getAnnotation(Operation.class);
                        if (!op.summary().isEmpty()) name = op.summary();
                    }

                    insertIfAbsent(name, code, mapping.method, fullPath, moduleNames[moduleNames.length - 1], parentId);
                }
            }
        }

        System.out.println("=== SysPermissionScanner：权限扫描完成 ===");
    }

    /* -------------------- 辅助方法 -------------------- */

    private void initGlobalPermissions() {
        insertIfAbsent("全局模块", "ALL", "*", "/**", "全局", null);
        insertIfAbsent("全局查看", "ALL_GET", "GET", "/**", "全局", 1L);
        insertIfAbsent("全局创建", "ALL_POST", "POST", "/**", "全局", 1L);
        insertIfAbsent("全局修改", "ALL_PUT", "PUT", "/**", "全局", 1L);
        insertIfAbsent("全局删除", "ALL_DELETE", "DELETE", "/**", "全局", 1L);
    }

    /**
     * @param str 需要分割的字符串
     * @param regex 正则分割符
     * @return /sys/role → ["sys", "role"]
     */
    private String[] resolveModuleHierarchy(String str, String regex) {
        return Arrays.stream(str.split(regex))
                .filter(s -> !s.isBlank())
                .toArray(String[]::new);
    }

    private Long initModulePermission(String code, String moduleName, String path, Long parentId) {
        return insertIfAbsent(
                moduleName + "模块",
                code,
                "*",
                path,
                moduleName,
                parentId
        );
    }

    private Long insertIfAbsent(String name, String code,
                                String method, String path,
                                String moduleName, Long parentId) {

        SysPermission p = sysPermissionMapper.selectOne(
                new QueryWrapper<SysPermission>()
                        .eq("code", code)
                        .eq("method", method)
                        .eq("path", path)
        );

        AuthLevel level = authLevelResolver.resolve(path);

        if (p == null) {
            p = new SysPermission();
            p.setCode(code);
            p.setMethod(method);
            p.setPath(path);
        }
        p.setName(name);
        p.setModuleName(moduleName);
        p.setAuthLevel(level.getCode());
        p.setParentId(parentId);
        p.setStatus(true);

        if (p.getId() != null) {
            sysPermissionMapper.updateById(p);
        } else {
            sysPermissionMapper.insert(p);
        }

        return p.getId();
    }

    private MappingInfo resolveMapping(Method method) {

        if (method.isAnnotationPresent(GetMapping.class)) {
            return new MappingInfo("GET", method.getAnnotation(GetMapping.class).value());
        }
        if (method.isAnnotationPresent(PostMapping.class)) {
            return new MappingInfo("POST", method.getAnnotation(PostMapping.class).value());
        }
        if (method.isAnnotationPresent(PutMapping.class)) {
            return new MappingInfo("PUT", method.getAnnotation(PutMapping.class).value());
        }
        if (method.isAnnotationPresent(DeleteMapping.class)) {
            return new MappingInfo("DELETE", method.getAnnotation(DeleteMapping.class).value());
        }
        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping rm = method.getAnnotation(RequestMapping.class);
            String m = rm.method().length > 0 ? rm.method()[0].name() : "GET";
            return new MappingInfo(m, rm.value());
        }
        return null;
    }

    record MappingInfo(String method, String[] paths) {
            MappingInfo(String method, String[] paths) {
                this.method = method;
                this.paths = (paths == null || paths.length == 0)
                        ? new String[]{""}
                        : paths;
            }
        }
}