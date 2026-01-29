package com.xusheng94.leyu.admin.service;

import com.xusheng94.leyu.admin.entity.SysRole;
import com.xusheng94.leyu.admin.entity.SysUserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统--用户-角色关联表 服务类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-10 01:41:52
 */
public interface ISysUserRoleService extends IService<SysUserRole> {

    Map<Long, List<SysRole>> getRolesByUserIds(List<Long> userIds);
}
