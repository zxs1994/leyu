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
    public AuthLevel resolve(String path) {

        // 仅平台
        for (String p : securityProperties.getPlatformOnlyUrls()) {
            if (antPathMatcher.match(p, path)) {
                return AuthLevel.PLATFORM_ONLY;
            }
        }

        // 登录即可
        for (String p : securityProperties.getLoginOnlyUrls()) {
            if (antPathMatcher.match(p, path)) {
                return AuthLevel.LOGIN_ONLY;
            }
        }

        // 白名单
        for (String p : securityProperties.getWhitelistUrls()) {
            if (antPathMatcher.match(p, path)) {
                return AuthLevel.WHITELIST;
            }
        }

        // 默认
        return AuthLevel.NORMAL;
    }
}
