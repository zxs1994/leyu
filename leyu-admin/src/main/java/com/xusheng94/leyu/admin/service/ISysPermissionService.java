package com.xusheng94.leyu.admin.service;

import com.xusheng94.leyu.admin.model.vo.SysPermissionTreeNode;
import com.xusheng94.leyu.admin.entity.SysPermission;
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

    List<SysPermissionTreeNode> getTree();

    List<String> getCodesByUserId(Long userId);
}
