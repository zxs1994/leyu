package com.github.zxs1994.java_template.config;

import com.github.zxs1994.java_template.config.security.SecurityProperties;
import com.github.zxs1994.java_template.enums.AuthLevel;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component
public class AuthLevelResolver {

    private final SecurityProperties securityProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public AuthLevelResolver(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public AuthLevel resolve(String path) {

        // 1️⃣ 白名单
        for (String p : securityProperties.getWhitelistUrls()) {
            if (pathMatcher.match(p, path)) {
                return AuthLevel.WHITELIST;
            }
        }

        // 2️⃣ 登录即可
        for (String p : securityProperties.getLoginOnlyUrls()) {
            if (pathMatcher.match(p, path)) {
                return AuthLevel.LOGIN_ONLY;
            }
        }

        // 3️⃣ 默认
        return AuthLevel.NORMAL;
    }
}
