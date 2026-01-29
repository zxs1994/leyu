package com.xusheng94.leyu.admin.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    /**
     * 白名单（无需登录）
     */
    private List<String> whitelistUrls = new ArrayList<>();

    /**
     * 登录即可访问
     */
    private List<String> loginOnlyUrls = new ArrayList<>();

    /**
     * 仅平台用户可访问
     */
    private List<String> platformOnlyUrls = new ArrayList<>();

}
