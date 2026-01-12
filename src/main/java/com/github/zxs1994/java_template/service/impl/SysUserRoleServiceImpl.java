package com.github.zxs1994.java_template.service.impl;

import com.github.zxs1994.java_template.entity.SysUserRole;
import com.github.zxs1994.java_template.mapper.SysUserRoleMapper;
import com.github.zxs1994.java_template.service.ISysUserRoleService;
import com.github.zxs1994.java_template.service.SystemProtectService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统--用户-角色关联表 服务实现类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-10 01:41:52
 */
@Service
public class SysUserRoleServiceImpl extends SystemProtectService<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

}
