package com.xusheng94.leyu.admin.config.security.jwt;

import com.xusheng94.leyu.admin.entity.SysUser;
import com.xusheng94.leyu.admin.mapper.SysUserMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTH_EXPIRED_ATTR = "AUTH_EXPIRED";

    private final JwtUtils jwtUtils;

    private final SysUserMapper sysUserMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = jwtUtils.resolveToken(request);

        if (token != null) {
            // 先区分可用 token 与已过期 token（其他非法 token 直接忽略）。
            boolean validToken = jwtUtils.validateToken(token);
            boolean expiredToken = !validToken && jwtUtils.isTokenExpired(token);

            if (validToken || expiredToken) {
                // 两条分支都需要用户与版本号校验，统一在外层提取，避免重复查询。
                Long sysUserId = jwtUtils.getSysUserIdFromToken(token);
                Integer tokenVersion = jwtUtils.getTokenVersion(token);
                SysUser sysUser = sysUserMapper.selectById(sysUserId);

                boolean versionMatch = sysUser != null && tokenVersion != null
                        && tokenVersion.equals(sysUser.getTokenVersion());
                // 版本号匹配时写入认证信息，过期 token 也要让后续日志能拿到操作者。
                if (versionMatch) {
                    log.debug("sysUserId = {}", sysUserId);
                    UsernamePasswordAuthenticationToken auth = jwtUtils.getAuthentication(token);
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
                // 过期且版本号匹配时，额外标记为过期，供刷新逻辑使用。
                if (versionMatch && expiredToken) {
                    request.setAttribute(AUTH_EXPIRED_ATTR, true);
                }
            }
        }

        // ⚠️ 无论有没有 token，都要继续往下走
        filterChain.doFilter(request, response);
    }
}