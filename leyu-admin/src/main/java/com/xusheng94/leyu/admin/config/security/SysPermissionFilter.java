package com.xusheng94.leyu.admin.config.security;

import com.xusheng94.leyu.common.config.IAuthLevelResolver;
import com.xusheng94.leyu.common.ApiResponse;
import com.xusheng94.leyu.admin.cache.SysPermissionCache;
import com.xusheng94.leyu.admin.config.security.jwt.JwtAuthenticationFilter;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SysPermissionFilter extends OncePerRequestFilter {

    private final SysPermissionMapper sysPermissionMapper;
    private final SysPermissionCache sysPermissionCache;
    private final IAuthLevelResolver authLevelResolver;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // System.out.println("SysPermissionFilter checking, method=" + method + ", path=" + path);
        SysPermission sysPermission = SysPermissionMatcher.matchExact(
                sysPermissionCache.listAll(), method, path);

        // 1️⃣ 没有匹配的权限路径 可能是404或者swagger-ui之类的接口，直接放行，由后续的机制处理（如404）
        // 注意：不能在这里直接返回404，因为可能是 swagger-ui 之类的接口，这些接口不需要权限，但确实没有权限数据
        // 所以这里的机制是：如果没有权限数据，直接放行，由后续的机制处理（如404），而不是在这里直接返回404
        if (sysPermission == null) {
            filterChain.doFilter(request, response);
            return;
        }

        AuthLevel authLevel = authLevelResolver.resolve(method, path);

        // 2️⃣ 白名单直接放行
        if (authLevel == AuthLevel.WHITELIST) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3️⃣ access token 已过期时，统一返回 498 (刷新接口已在白名单)，触发前端刷新流程。
        if (Boolean.TRUE.equals(request.getAttribute(JwtAuthenticationFilter.AUTH_EXPIRED_ATTR))) {
            throw new AuthenticationCredentialsNotFoundException(null);
        }

        // 4️⃣ 需要登录（LOGIN_ONLY / NORMAL / PLATFORM_ONLY）
        if (!CurrentUser.isLogin()) {
            String message = ApiResponse.fail(HttpServletResponse.SC_UNAUTHORIZED).getMsg();
            request.setAttribute(SysOperationLogFilter.OP_LOG_ERROR_MSG_ATTR, message);
            throw new AuthenticationCredentialsNotFoundException(message);
        }

        // 5️⃣ 平台专属接口：仅平台用户可访问
        if (authLevel == AuthLevel.PLATFORM_ONLY && !CurrentUser.isPlatformUser()) {
            String message = resolveForbiddenMessage(sysPermission);
            request.setAttribute(SysOperationLogFilter.OP_LOG_ERROR_MSG_ATTR, message);
            throw new AccessDeniedException(message);
        }

        // 6️⃣ 登录即可
        if (authLevel == AuthLevel.LOGIN_ONLY) {
            filterChain.doFilter(request, response);
            return;
        }

        // 7️⃣ NORMAL：需要权限校验
        Long userId = CurrentUser.getUserId();
        List<SysPermission> userPermissions = sysPermissionMapper.selectByUserId(userId);

        SysPermission matched = userPermissions.stream()
                .filter(p -> SysPermissionMatcher.match(p, method, path))
                .findFirst()
                .orElse(null);

        if (matched != null) {
            filterChain.doFilter(request, response);
        } else {
            String message = resolveForbiddenMessage(sysPermission);
            request.setAttribute(SysOperationLogFilter.OP_LOG_ERROR_MSG_ATTR, message);
            throw new AccessDeniedException(message);
        }
    }

    private String resolveForbiddenMessage(SysPermission requiredPermission) {
        if (requiredPermission != null && requiredPermission.getName() != null
                && !requiredPermission.getName().isBlank()) {
            return "没有【" + requiredPermission.getName() + "】权限";
        }
        return ApiResponse.fail(HttpServletResponse.SC_FORBIDDEN).getMsg();
    }

}