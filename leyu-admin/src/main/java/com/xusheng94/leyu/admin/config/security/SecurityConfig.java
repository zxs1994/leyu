package com.xusheng94.leyu.admin.config.security;

import com.xusheng94.leyu.admin.common.cache.SysPermissionCache;
import com.xusheng94.leyu.admin.common.ApiResponse;
import com.xusheng94.leyu.admin.config.security.jwt.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.xusheng94.leyu.admin.entity.SysPermission;
import com.xusheng94.leyu.admin.common.util.SysPermissionMatcher;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableAutoConfiguration(exclude = {UserDetailsServiceAutoConfiguration.class})
public class SecurityConfig {

    private final SecurityProperties securityProperties;
    private final SysPermissionFilter sysPermissionFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SysPermissionCache sysPermissionCache;

    // 注册密码加密 Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 跨域配置
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*"); // 或指定域名
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.addAllowedHeader("*"); // 或只写你需要的头
        // allowCredentials 默认 false
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            ObjectMapper objectMapper) throws Exception {

        http
            // 让 Spring Security 应用 corsConfigurationSource 配置
            .cors(Customizer.withDefaults())
            // 禁用 CSRF，因为我们用 JWT
            .csrf(AbstractHttpConfigurer::disable)
            // 不使用表单登录或 HTTP Basic
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)

            // 权限配置
            .authorizeHttpRequests(auth -> auth
                    // 白名单（Spring Security 级别）
                    .requestMatchers(securityProperties.getWhitelistUrls().toArray(String[]::new))
                    .permitAll()

                    // 其他一律要求登录（兜底）
                    .anyRequest().authenticated()
            )

            // 🔐 谁是谁 → before JWT 过滤器放在 UsernamePasswordAuthenticationFilter 前
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            // 🔑 能不能 → after 权限过滤
            .addFilterAfter(sysPermissionFilter, JwtAuthenticationFilter.class)

            // 返回 JSON 而不是默认 HTML 登录页
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((req, res, e) ->
                            handleAuthError(req, res, objectMapper, e))
                    .accessDeniedHandler((req, res, e) ->
                            handleAuthError(req, res, objectMapper, e))
            );

        return http.build();
    }

    /**
     * ⭐ 统一权限 / 认证错误输出
     */
    private void handleAuthError(
            HttpServletRequest request,
            HttpServletResponse response,
            ObjectMapper objectMapper,
            Exception e
    ) throws IOException {
        int code = response.getStatus();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        ApiResponse<?> body;

        if (code == HttpServletResponse.SC_FORBIDDEN) {
            String method = request.getMethod();
            // 原始uri
            String originalUri = (String) request.getAttribute(
                    RequestDispatcher.ERROR_REQUEST_URI
            );

            SysPermission rule = SysPermissionMatcher.matchExactThenGlobal(sysPermissionCache.listAll(), method, originalUri);

            log.info("Permission check start: method={}, uri={}", method, originalUri);
            log.info("Matched rule: {}", rule);

            if (rule != null) {
                body = ApiResponse.fail(code, rule,"没有 " + rule.getName() + " 权限");
            } else {
                body = ApiResponse.fail(code);
            }

        } else {
            body = ApiResponse.fail(code);
        }

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

}