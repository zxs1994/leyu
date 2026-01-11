package com.github.zxs1994.java_template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.zxs1994.java_template.dto.PermissionTreeNode;
import com.github.zxs1994.java_template.entity.SysPermission;
import com.github.zxs1994.java_template.mapper.SysPermissionMapper;
import com.github.zxs1994.java_template.service.ISysPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统--权限表 服务实现类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-10 01:41:52
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements ISysPermissionService {
    @Autowired
    SysPermissionMapper sysPermissionMapper;

    @Override
    public List<PermissionTreeNode> getPermissionTree() {

        // 1️⃣ 查询所有未删除权限
        List<SysPermission> permissions = sysPermissionMapper.selectList(
                new QueryWrapper<SysPermission>()
                        .eq("del", 0)
                        .orderByAsc("id")
        );

        // 2️⃣ 转成 nodeMap（id -> node）
        Map<Long, PermissionTreeNode> nodeMap = getTreeNodeMap(permissions);

        // 3️⃣ 组装成树
        List<PermissionTreeNode> roots = new ArrayList<>();

        for (SysPermission p : permissions) {
            PermissionTreeNode current = nodeMap.get(p.getId());

            Long parentId = p.getParentId();
            if (parentId == null || parentId == 0) {
                // 顶级节点
                roots.add(current);
            } else {
                PermissionTreeNode parent = nodeMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(current);
                }
            }
        }

        return roots;
    }

    private static Map<Long, PermissionTreeNode> getTreeNodeMap(List<SysPermission> permissions) {
        Map<Long, PermissionTreeNode> nodeMap = new LinkedHashMap<>();

        for (SysPermission p : permissions) {
            PermissionTreeNode node = new PermissionTreeNode();
            node.setId(p.getId());
            node.setCode(p.getCode());
            node.setName(p.getName());
            node.setPath(p.getPath());
            node.setMethod(p.getMethod());
            node.setAuthLevel(p.getAuthLevel());
            node.setParentId(p.getParentId());

            nodeMap.put(p.getId(), node);
        }
        return nodeMap;
    }
}
