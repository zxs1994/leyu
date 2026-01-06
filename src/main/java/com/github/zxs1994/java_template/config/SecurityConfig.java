package com.example.template.config;

import com.example.template.common.ApiResponse;
import com.example.template.util.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableAutoConfiguration(exclude = {UserDetailsServiceAutoConfiguration.class})
public class SecurityConfig {

    // 注册密码加密 Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 从配置文件读取放行的 URL（逗号分隔）
    @Value("${security.permit-urls}")
    private String permitUrls;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            JwtUtils jwtUtils,
            ObjectMapper objectMapper) throws Exception {

        // 转成数组，直接用于 requestMatchers
        String[] urls = Arrays.stream(permitUrls.split(","))
                .map(String::trim)
                .toArray(String[]::new);

        // 创建 JWT 过滤器实例
        JwtAuthenticationFilter jwtFilter =
                new JwtAuthenticationFilter(jwtUtils, objectMapper);

        http
                // 禁用 CSRF，因为我们用 JWT
                .csrf(csrf -> csrf.disable())

                // 不使用表单登录或 HTTP Basic
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 权限配置
                .authorizeHttpRequests(auth ->
                                auth.requestMatchers(urls).permitAll()
                    .anyRequest().authenticated()
                )

                // JWT 过滤器放在 UsernamePasswordAuthenticationFilter 前
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                // 返回 JSON 而不是默认 HTML 登录页
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_OK);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write(objectMapper.writeValueAsString(
                                    ApiResponse.fail(401, null, "未登录或 Token 无效")
                            ));
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_OK);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write(objectMapper.writeValueAsString(
                                    ApiResponse.fail(403, null, "没有权限")
                            ));
                        })
                );

        return http.build();
    }
}