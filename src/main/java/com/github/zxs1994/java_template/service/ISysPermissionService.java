package com.github.zxs1994.java_template.service;

import com.github.zxs1994.java_template.dto.PermissionTreeNode;
import com.github.zxs1994.java_template.entity.SysPermission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 系统--权限表 服务类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-10 01:41:52
 */
public interface ISysPermissionService extends IService<SysPermission> {
    List<PermissionTreeNode> getPermissionTree();
}
