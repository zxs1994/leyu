package com.xusheng94.leyu.admin.config;

import com.xusheng94.leyu.admin.config.security.SecurityProperties;
import com.xusheng94.leyu.common.enums.AuthLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import com.xusheng94.leyu.common.config.IAuthLevelResolver;

@Component
@RequiredArgsConstructor
public class AuthLevelResolver implements IAuthLevelResolver {

    private final SecurityProperties securityProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 顺序很重要：更严格的规则必须优先匹配，避免被宽松规则覆盖
     */
    @Override
    public AuthLevel resolve(String path) {
        return resolve(null, path);
    }

    @Override
    public AuthLevel resolve(String method, String path) {

        // 仅平台
        for (String p : securityProperties.getPlatformOnlyUrls()) {
            if (matchRule(p, method, path)) {
                return AuthLevel.PLATFORM_ONLY;
            }
        }

        // 登录即可
        for (String p : securityProperties.getLoginOnlyUrls()) {
            if (matchRule(p, method, path)) {
                return AuthLevel.LOGIN_ONLY;
            }
        }

        // 白名单
        for (String p : securityProperties.getWhitelistUrls()) {
            if (matchRule(p, method, path)) {
                return AuthLevel.WHITELIST;
            }
        }

        // 默认
        return AuthLevel.NORMAL;
    }

    /**
     * 兼容两种规则写法：
     * 1) /auth/**            -> 仅按 path 匹配
     * 2) POST /auth/login    -> 按 method + path 匹配
     */
    private boolean matchRule(String rule, String method, String path) {
        if (rule == null) {
            return false;
        }

        String trimmed = rule.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        // path-only 规则（以 / 开头）
        if (trimmed.startsWith("/")) {
            return antPathMatcher.match(trimmed, path);
        }

        // method+path 规则："METHOD /pattern"
        int firstSpace = trimmed.indexOf(' ');
        if (firstSpace <= 0 || firstSpace >= trimmed.length() - 1) {
            return antPathMatcher.match(trimmed, path);
        }

        String ruleMethod = trimmed.substring(0, firstSpace).trim();
        String rulePath = trimmed.substring(firstSpace + 1).trim();
        if (rulePath.isEmpty()) {
            return false;
        }

        if (method == null || method.isBlank()) {
            return false;
        }

        return ruleMethod.equalsIgnoreCase(method)
                && antPathMatcher.match(rulePath, path);
    }
}
