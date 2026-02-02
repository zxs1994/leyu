package com.xusheng94.leyu.admin.config.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 密钥
     */
    @Setter
    private String secret;

    /**
     * 过期天数（从配置文件读取）
     */
    private int expireDays;

    /**
     * 过期毫秒数（计算得出，不从配置读）
     */
    private long expireMillis;

    /**
     * refresh token 过期天数（从配置文件读取）
     */
    private int refreshExpireDays;

    /**
     * refresh token 过期毫秒数（计算得出，不从配置读）
     */
    private long refreshExpireMillis;

    public void setExpireDays(int expireDays) {
        this.expireDays = expireDays;
        // ⚠️ 一定要用 long 参与计算
        this.expireMillis = expireDays * 24L * 60 * 60 * 1000;
    }

    public void setRefreshExpireDays(int refreshExpireDays) {
        this.refreshExpireDays = refreshExpireDays;
        this.refreshExpireMillis = refreshExpireDays * 24L * 60 * 60 * 1000;
    }

}