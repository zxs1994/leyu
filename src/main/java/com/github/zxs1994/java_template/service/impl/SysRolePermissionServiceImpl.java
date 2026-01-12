package com.github.zxs1994.java_template.service.impl;

import com.github.zxs1994.java_template.entity.SysRolePermission;
import com.github.zxs1994.java_template.mapper.SysRolePermissionMapper;
import com.github.zxs1994.java_template.service.ISysRolePermissionService;
import com.github.zxs1994.java_template.service.SystemProtectService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统--角色-权限关联表 服务实现类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-10 01:41:52
 */
@Service
public class SysRolePermissionServiceImpl extends SystemProtectService<SysRolePermissionMapper, SysRolePermission> implements ISysRolePermissionService {

}
