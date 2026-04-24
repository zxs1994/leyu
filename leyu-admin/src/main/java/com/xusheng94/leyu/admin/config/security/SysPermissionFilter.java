package com.xusheng94.leyu.admin.config.security;

import com.xusheng94.leyu.common.config.IAuthLevelResolver;
import com.xusheng94.leyu.common.ApiResponse;
import com.xusheng94.leyu.admin.cache.SysPermissionCache;
import com.xusheng94.leyu.admin.entity.SysPermission;
import com.xusheng94.leyu.common.enums.AuthLevel;
import com.xusheng94.leyu.admin.mapper.SysPermissionMapper;

import com.xusheng94.leyu.admin.util.CurrentUser;
import com.xusheng94.leyu.admin.util.SysPermissionMatcher;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SysPermissionFilter extends OncePerRequestFilter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final SysPermissionMapper sysPermissionMapper;
    private final SysPermissionCache sysPermissionCache;
    private final IAuthLevelResolver authLevelResolver;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        if (shouldBypass(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        AuthLevel authLevel = authLevelResolver.resolve(path);

        // 1️⃣ 白名单直接放行
        if (authLevel == AuthLevel.WHITELIST) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2️⃣ 需要登录（LOGIN_ONLY / NORMAL / PLATFORM_ONLY）
        if (!CurrentUser.isLogin()) {
            request.setAttribute(SysOperationLogFilter.OP_LOG_ERROR_MSG_ATTR,
                    ApiResponse.fail(HttpServletResponse.SC_UNAUTHORIZED).getMsg());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 3️⃣ 平台专属接口：仅平台用户可访问
        if (authLevel == AuthLevel.PLATFORM_ONLY && !CurrentUser.isPlatformUser()) {
            request.setAttribute(SysOperationLogFilter.OP_LOG_ERROR_MSG_ATTR,
                    resolveForbiddenMessage(method, path));
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // 4️⃣ 登录即可
        if (authLevel == AuthLevel.LOGIN_ONLY) {
            filterChain.doFilter(request, response);
            return;
        }

        // 5️⃣ NORMAL：需要权限校验
        Long userId = CurrentUser.getUserId();
        List<SysPermission> userPermissions = sysPermissionMapper.selectByUserId(userId);

        SysPermission matched = userPermissions.stream()
                .filter(p -> SysPermissionMatcher.match(p, method, path))
                .findFirst()
                .orElse(null);

        if (matched != null) {
            filterChain.doFilter(request, response);
        } else {
            request.setAttribute(SysOperationLogFilter.OP_LOG_ERROR_MSG_ATTR,
                    resolveForbiddenMessage(method, path));
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private String resolveForbiddenMessage(String method, String path) {
        SysPermission requiredPermission = SysPermissionMatcher.matchExactThenGlobal(
                sysPermissionCache.listAll(), method, path);
        if (requiredPermission != null && requiredPermission.getName() != null
                && !requiredPermission.getName().isBlank()) {
            return "没有【" + requiredPermission.getName() + "】权限";
        }
        return ApiResponse.fail(HttpServletResponse.SC_FORBIDDEN).getMsg();
    }

    private boolean shouldBypass(String path) {
        List<SysPermission> permissions = sysPermissionCache.listAll();
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        return permissions.stream()
                .map(SysPermission::getPath)
                .filter(permissionPath -> permissionPath != null && !permissionPath.isBlank())
                .filter(permissionPath -> !"/**".equals(permissionPath))
                .noneMatch(permissionPath -> PATH_MATCHER.match(permissionPath, path));
    }
}