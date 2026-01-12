package com.github.zxs1994.java_template.config.security;

import com.github.zxs1994.java_template.config.AuthLevelResolver;
import com.github.zxs1994.java_template.entity.SysPermission;
import com.github.zxs1994.java_template.enums.AuthLevel;
import com.github.zxs1994.java_template.mapper.SysPermissionMapper;

import com.github.zxs1994.java_template.util.CurrentUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SysPermissionFilter extends OncePerRequestFilter {

    private final SysPermissionMapper sysPermissionMapper;
    private final AuthLevelResolver authLevelResolver;
    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        AuthLevel authLevel = authLevelResolver.resolve(path);

        // 1️⃣ 白名单直接放行
        if (authLevel == AuthLevel.WHITELIST) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2️⃣ 需要登录（LOGIN_ONLY + NORMAL 都要）
        if (!CurrentUser.isLogin()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 3️⃣ 登录即可
        if (authLevel == AuthLevel.LOGIN_ONLY) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId = CurrentUser.getId();

        // 4️⃣ NORMAL：需要权限校验
        List<SysPermission> userPermissions = sysPermissionMapper.selectByUserId(userId);

        // System.out.println(userPermissions.toString());

        // 先过滤 method
        List<SysPermission> filteredByMethod = userPermissions.stream()
                .filter(p -> p.getMethod().equals("*") || p.getMethod().equalsIgnoreCase(method))
                .toList();

        // 匹配权限：全局 / 模块总开关 / 动态接口 / 静态接口 都统一用 matcher
        SysPermission matched = filteredByMethod.stream()
                .filter(p -> p.getPath().equals("*") && p.getModule().equals("ALL") // 全局权限
                        || matcher.match(p.getPath(), path))                        // 其他接口
                .findFirst()
                .orElse(null);

        if (matched != null) {
            System.out.println(matched.toString());
            filterChain.doFilter(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}