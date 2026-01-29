package com.xusheng94.leyu.admin.config.security.jwt;

import com.xusheng94.leyu.admin.config.security.LoginUser;
import com.xusheng94.leyu.admin.entity.SysUser;
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

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

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
                .claim("tenantId", sysUser.getTenantId())     // ⭐ 租户ID
                .claim("tokenVersion", sysUser.getTokenVersion())
                .claim("source", sysUser.getSource())
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
     * 获取租户 ID
     */
    public Long getTenantIdFromToken(String token) {
        return parseToken(token).getBody().get("tenantId", Long.class);
    }

    /**
     * 获取用户 source
     */
    public String getSourceFromToken(String token) {
        return parseToken(token).getBody().get("source", String.class);
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
        String auth = request.getHeader(AUTH_HEADER);
        if (auth == null || !auth.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return auth.substring(BEARER_PREFIX.length());
    }

    /**
     * 根据 token 获取 Authentication 对象
     */
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        LoginUser loginUser = new LoginUser(
                getSysUserIdFromToken(token),
                getTenantIdFromToken(token),
                getSubjectFromToken(token),
                getSourceFromToken(token),
                getTokenVersion(token)
        );

        return new UsernamePasswordAuthenticationToken(
                loginUser,
                null,
                Collections.emptyList()
        );
    }

}