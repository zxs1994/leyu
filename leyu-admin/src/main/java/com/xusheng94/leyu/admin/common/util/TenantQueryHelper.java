package com.xusheng94.leyu.admin.common.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public class TenantQueryHelper {

    private TenantQueryHelper() {}

    /**
     * 角色 / 权限 / 可共享资源通用规则
     * tenant 可见自己的 + 系统的
     */
    public static <T> void tenantOrSystem(QueryWrapper<T> qw) {
        if (CurrentUser.isTenantUser()) {
            qw.and(w -> w
                    .eq("tenant_id", CurrentUser.getTenantId())
                    .or()
                    .isNull("tenant_id")
            );
        }
    }

    /**
     * 严格租户隔离（仅自己）
     */
    public static <T> void tenantOnly(QueryWrapper<T> qw) {
        if (CurrentUser.isTenantUser()) {
            qw.eq("tenant_id", CurrentUser.getTenantId());
        }
    }
}
