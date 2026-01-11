package com.github.zxs1994.java_template.service.impl;

import com.github.zxs1994.java_template.entity.SysRole;

import com.github.zxs1994.java_template.mapper.SysRoleMapper;
import com.github.zxs1994.java_template.service.ISysRoleService;
import com.github.zxs1994.java_template.service.SystemProtectService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 系统--角色表 服务实现类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-10 01:41:52
 */
@Service
public class SysRoleServiceImpl extends SystemProtectService<SysRoleMapper, SysRole> implements ISysRoleService {

}
