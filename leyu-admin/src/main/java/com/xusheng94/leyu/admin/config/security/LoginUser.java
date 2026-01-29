package com.xusheng94.leyu.admin.config.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginUser {

    private Long userId;
    private Long tenantId;
    private String email;
    private String source;
    private Integer tokenVersion;

    /**
     * 是否系统级用户（平台管理员）
     * tenantId == null 表示不属于任何租户
     */
    public boolean isPlatformUser() {
        return tenantId == null;
    }

    /**
     * 是否租户用户
     */
    public boolean isTenantUser() {
        return tenantId != null;
    }

    /**
     * 是否是系统创建的用户(顶级平台用户或者是顶级租户)
     */
    public boolean isSystemUser() {
        return source != null;
    }
}
