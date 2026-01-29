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

    private final JwtUtils jwtUtils;

    private final SysUserMapper sysUserMapper;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = jwtUtils.resolveToken(request);

        if (token != null && jwtUtils.validateToken(token)) {
            Long sysUserId = jwtUtils.getSysUserIdFromToken(token);
            Integer tokenVersion = jwtUtils.getTokenVersion(token);
            String email = jwtUtils.getSubjectFromToken(token);
            SysUser sysUser = sysUserMapper.selectById(sysUserId);

            log.debug("sysUserId = {}", sysUserId);
            if (sysUser != null 	                                   // 🚫 防已删除用户
                    && sysUser.getStatus()                             // 🚫 防已禁用用户
                    && tokenVersion.equals(sysUser.getTokenVersion())  // 🚫 防并发登录 / 踢人
                    && email.equals(sysUser.getEmail())                // 🚫 防敏感信息变更后 token 继续生效
            ) {
                UsernamePasswordAuthenticationToken auth = jwtUtils.getAuthentication(token);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        }


        // ⚠️ 无论有没有 token，都要继续往下走
        filterChain.doFilter(request, response);
    }
}