package com.github.zxs1994.java_template.config.security.jwt;

import com.github.zxs1994.java_template.entity.SysUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtUtils {

    private final JwtProperties jwtProperties;
    private final Key key;

    public JwtUtils(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    /**
     * 生成 JWT token，包含用户 ID、用户名和角色列表
     */
    public String generateToken(SysUser sysUser) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(sysUser.getEmail())
                .claim("id", sysUser.getId())             // 用户 ID
                .claim("tokenVersion", sysUser.getTokenVersion())
                .setIssuedAt(new Date(now))      // 签发时间
                .setExpiration(new Date(now + jwtProperties.getExpireMillis())) // 过期时间
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 获取Subject
     */
    public String getSubjectFromToken(String token) {
        return parseToken(token).getBody().getSubject();
    }

    /**
     * 获取用户 ID
     */
    public Long getSysUserIdFromToken(String token) {
        return parseToken(token).getBody().get("id", Long.class);
    }

    /**
     * 获取token版本
     */
    public Integer getTokenVersion(String token) {
        return parseToken(token).getBody().get("tokenVersion", Integer.class);
    }

    /**
     * 验证 token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 解析 token
     */
    private Jws<Claims> parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    /**
     * 从请求头获取 token
     */
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    /**
     * 根据 token 获取 Authentication 对象
     */
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Long sysUserId = getSysUserIdFromToken(token);
        return new UsernamePasswordAuthenticationToken(sysUserId, null, Collections.emptyList());
    }

}