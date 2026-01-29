package com.xusheng94.leyu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xusheng94.leyu.common.BaseQuery;
import com.xusheng94.leyu.common.BizException;
import com.xusheng94.leyu.entity.SysDept;
import com.xusheng94.leyu.entity.SysUser;
import com.xusheng94.leyu.mapper.SysDeptMapper;
import com.xusheng94.leyu.service.ISysDeptService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xusheng94.leyu.common.util.CurrentUser;
import com.xusheng94.leyu.common.util.TreeUtils;
import com.xusheng94.leyu.model.vo.SysDeptTreeNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统--组织部门表 服务实现类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-20 14:06:31
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    private final SysUserServiceImpl sysUserService;

    @Override
    @Transactional
    public boolean removeById(Long id) {

        // 1️⃣ 查询部门（带租户隔离）
        SysDept dept = getById(id);
        if (dept == null) {
            throw new BizException(404, "部门不存在");
        }

        // 2️⃣ 顶级部门不能删
        if (dept.getParentId() == null) {
            if (CurrentUser.isTenantUser()) {
                throw new BizException(403, "顶级部门不能删除");
            }
        }

        // 3️⃣ 是否存在子部门
        boolean hasChildren = this.count(
                new LambdaQueryWrapper<SysDept>()
                        .eq(SysDept::getParentId, id)
        ) > 0;

        if (hasChildren) {
            throw new BizException(403, "请先删除子部门");
        }

        // 4️⃣ 是否有用户关联
        boolean hasUsers = sysUserService.count(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getDeptId, id)
        ) > 0;

        if (hasUsers) {
            throw new BizException(403, "该部门下存在用户，无法删除");
        }

        // 5️⃣ 逻辑删除
        return super.removeById(id);
    }

    @Override
    @Transactional
    public boolean save(SysDept sysDept) {

        if (CurrentUser.isPlatformUser()) {
            throw new BizException(400, "请切换到当前租户操作");
        }

        sysDept.setTenantId(CurrentUser.getTenantId());

        // 1️⃣ 如果还没ID，生成一个雪花ID
        if (sysDept.getId() == null) {
            sysDept.setId(IdWorker.getId()); // MyBatis-Plus 的雪花ID生成器
        }

        // 2️⃣ 计算 path
        if (sysDept.getParentId() == null) {
            // 顶级部门
            sysDept.setPath("/" + sysDept.getId());
        } else {
            SysDept parent = getById(sysDept.getParentId());
            if (parent == null) {
                throw new BizException(400, "父部门不存在");
            }
            // path = 父 path + "/" + 当前id
            sysDept.setPath(parent.getPath() + "/" + sysDept.getId());
        }

        // 3️⃣ 直接保存
        boolean flag = super.save(sysDept);
        if (!flag) {
            throw new BizException(500, "部门添加失败");
        }

        return true;
    }

    @Override
    public List<SysDeptTreeNode> getTree() {
        // 1️⃣ 查询部门 + 用户
        List<SysDept> allDepts = list();
        List<SysUser> allUsers = sysUserService.list();

        // 2️⃣ 构建用户映射：用户ID → 用户对象
        Map<Long, SysUser> userMap = allUsers.stream()
                .collect(Collectors.toMap(SysUser::getId, u -> u));

        // 3️⃣ 转换为树节点
        List<SysDeptTreeNode> nodes = allDepts.stream().map(dept -> {
            SysDeptTreeNode node = new SysDeptTreeNode();
            BeanUtils.copyProperties(dept, node);

            // 3a️⃣ 设置负责人名称
            if (dept.getLeaderId() != null) {
                SysUser leader = userMap.get(dept.getLeaderId());
                node.setLeaderName(leader != null ? leader.getName() : null);
            }

            // 3b️⃣ 挂载部门下的用户列表
            node.setUsers(allUsers.stream()
                    .filter(u -> Objects.equals(u.getDeptId(), dept.getId()))
                    .toList());

            return node;
        }).toList();

        // 4️⃣ 构建树，以 parentId = null 或 0 作为根
        return TreeUtils.buildTree(nodes);
    }

    @Override
    public Page<SysDept> page(BaseQuery query) {
        QueryWrapper<SysDept> qw = new QueryWrapper<>();
        qw.orderByAsc("sort");
        qw.like(StringUtils.hasText(query.getName()), "name", query.getName());

        return super.page(new Page<>(query.getPage(), query.getSize()), qw);
    }

    @Override
    public List<SysDept> list() {
        QueryWrapper<SysDept> qw = new QueryWrapper<>();
        qw.orderByAsc("sort");
        return super.list(qw);
    }
}
