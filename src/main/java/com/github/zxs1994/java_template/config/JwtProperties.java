package com.example.template.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 密钥
     */
    private String secret;

    /**
     * 过期天数（从配置文件读取）
     */
    private int expireDays;

    /**
     * 默认角色
     */
    private String defaultRole;

    /**
     * 过期毫秒数（计算得出，不从配置读）
     */
    private long expireMillis;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getExpireDays() {
        return expireDays;
    }

    public void setExpireDays(int expireDays) {
        this.expireDays = expireDays;
        // ⚠️ 一定要用 long 参与计算
        this.expireMillis = expireDays * 24L * 60 * 60 * 1000;
    }

    public long getExpireMillis() {
        return expireMillis;
    }

    public String getDefaultRole() {
        return defaultRole;
    }

    public void setDefaultRole(String defaultRole) {
        this.defaultRole = defaultRole;
    }
}