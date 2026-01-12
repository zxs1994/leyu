package com.github.zxs1994.java_template.config.security.jwt;

import com.github.zxs1994.java_template.entity.SysUser;
import com.github.zxs1994.java_template.mapper.SysUserMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    private final SysUserMapper sysUserMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = jwtUtils.resolveToken(request);

            if (token != null && jwtUtils.validateToken(token)) {
                Long sysUserId = jwtUtils.getSysUserIdFromToken(token);
                Integer tokenVersion = jwtUtils.getTokenVersion(token);

                SysUser sysUser = sysUserMapper.selectById(sysUserId);

                if (tokenVersion.equals(sysUser.getTokenVersion())) {  // 单点登录
                    UsernamePasswordAuthenticationToken auth = jwtUtils.getAuthentication(token);
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }

            }


        // ⚠️ 无论有没有 token，都要继续往下走
        filterChain.doFilter(request, response);
    }
}