package com.xusheng94.leyu.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xusheng94.leyu.admin.common.BaseQuery;
import com.xusheng94.leyu.admin.entity.SysDept;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xusheng94.leyu.admin.model.vo.SysDeptTreeNode;

import java.util.List;

/**
 * <p>
 * 系统--组织部门表 服务类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-20 14:06:31
 */
public interface ISysDeptService extends IService<SysDept> {

    boolean removeById(Long id);

    boolean save(SysDept sysDept);

    List<SysDeptTreeNode> getTree();

    Page<SysDept> page(BaseQuery query);
}
