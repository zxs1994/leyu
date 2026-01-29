package com.xusheng94.leyu.admin.config;

import com.xusheng94.leyu.admin.config.security.SecurityProperties;
import com.xusheng94.leyu.admin.common.enums.AuthLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component
@RequiredArgsConstructor
public class AuthLevelResolver {

    private final SecurityProperties securityProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public AuthLevel resolve(String path) {

        // 白名单
        for (String p : securityProperties.getWhitelistUrls()) {
            if (antPathMatcher.match(p, path)) {
                return AuthLevel.WHITELIST;
            }
        }

        // 登录即可
        for (String p : securityProperties.getLoginOnlyUrls()) {
            if (antPathMatcher.match(p, path)) {
                return AuthLevel.LOGIN_ONLY;
            }
        }

        // 仅平台
        for (String p : securityProperties.getPlatformOnlyUrls()) {
            if (antPathMatcher.match(p, path)) {
                return AuthLevel.PLATFORM_ONLY;
            }
        }

        // 默认
        return AuthLevel.NORMAL;
    }
}
