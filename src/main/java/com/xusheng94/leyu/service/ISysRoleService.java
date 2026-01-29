package com.xusheng94.leyu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xusheng94.leyu.common.BaseQuery;
import com.xusheng94.leyu.model.dto.SysRoleDto;
import com.xusheng94.leyu.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xusheng94.leyu.model.vo.SysRoleVo;

/**
 * <p>
 * 系统--角色表 服务类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-10 01:41:52
 */
public interface ISysRoleService extends IService<SysRole> {

    Long save(SysRoleDto dto);

    boolean removeById(Long id);

    SysRole getById(Long id);

    Page<SysRoleVo> page(BaseQuery query);

    boolean updateById(SysRoleDto dto);
}
