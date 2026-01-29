package com.xusheng94.leyu.common.cache;

import com.xusheng94.leyu.entity.SysPermission;
import com.xusheng94.leyu.common.enums.AuthLevel;
import com.xusheng94.leyu.mapper.SysPermissionMapper;
import com.xusheng94.leyu.common.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SysPermissionCache {

    private final SysPermissionMapper sysPermissionMapper;
    /**
     * 所有启用的权限（不可变快照）
     */
    private volatile List<SysPermission> allPermissions;

    public void reload() {
        this.allPermissions = sysPermissionMapper.selectList(null);
    }

    /**
     * 给 Service / 前端用
     */
    public List<SysPermission> listAll() {
        return allPermissions;
    }

    /**
     * 获取【当前用户可见的】权限列表
     * - 用于列表 / 下拉 / 展示
     * - 租户用户自动过滤 platform-only 权限
     */
    public List<SysPermission> listVisiblePermissionsForCurrentUser() {
        return allPermissions.stream()
                .filter(p -> {
                    AuthLevel level = AuthLevel.fromCode(p.getAuthLevel());
                    return !level.platformOnly() || CurrentUser.isPlatformUser();
                })
                .toList();
    }

    /**
     * 获取【用于权限树的】权限列表（可分配）
     * - 仅返回参与权限校验的权限
     * - 租户用户自动过滤 platform-only 权限
     *
     * 用于：角色配置 / 权限树
     */
    public List<SysPermission> listAssignablePermissionsForCurrentUser() {
        return allPermissions.stream()
                .filter(p -> {
                    AuthLevel level = AuthLevel.fromCode(p.getAuthLevel());
                    return level.needPermissionCheck()
                            && (!level.platformOnly() || CurrentUser.isPlatformUser());
                })
                .toList();
    }
}
