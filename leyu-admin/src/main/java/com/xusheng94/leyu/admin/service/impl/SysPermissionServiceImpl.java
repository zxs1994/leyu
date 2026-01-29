package com.xusheng94.leyu.admin.service.impl;

import com.xusheng94.leyu.admin.common.cache.SysPermissionCache;
import com.xusheng94.leyu.admin.common.util.SysPermissionMatcher;
import com.xusheng94.leyu.admin.common.util.TreeUtils;
import com.xusheng94.leyu.admin.model.vo.SysPermissionTreeNode;
import com.xusheng94.leyu.admin.entity.SysPermission;
import com.xusheng94.leyu.admin.mapper.SysPermissionMapper;
import com.xusheng94.leyu.admin.service.ISysPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 系统--权限表 服务实现类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-10 01:41:52
 */
@RequiredArgsConstructor
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements ISysPermissionService {

    private final SysPermissionMapper sysPermissionMapper;
    private final SysPermissionCache sysPermissionCache;

    @Override
    public List<SysPermissionTreeNode> getTree() {

        // 1️⃣ 查询所有可选择的权限
        List<SysPermission> allPermissions = sysPermissionCache.listAssignablePermissionsForCurrentUser();

        // 2️⃣ 转成树节点
        List<SysPermissionTreeNode> nodes = allPermissions.stream().map(p -> {
            SysPermissionTreeNode node = new SysPermissionTreeNode();
            BeanUtils.copyProperties(p, node);
            return node;
        }).toList();

        // 3️⃣ 构建树，根节点 parent_id = null
        return TreeUtils.buildTree(nodes);
    }

    @Override
    public List<String> getCodesByUserId(Long userId) {

        List<SysPermission> userPermissions =
                sysPermissionMapper.selectByUserId(userId);

        List<SysPermission> allPermissions =
                sysPermissionCache.listVisiblePermissionsForCurrentUser();

        Set<String> result = new HashSet<>();

        for (SysPermission userPerm : userPermissions) {
            for (SysPermission perm : allPermissions) {
                // 只给前端用的，必须有 code
                if (perm.getCode() == null || perm.getCode().isBlank()) {
                    continue;
                }

                // permission.code = 授权能力 不是 是否可使用功能, 所以权限code才给前端
                // ⭐ 核心：权限规则匹配
                if (SysPermissionMatcher.cover(userPerm, perm)) {
                    result.add(perm.getCode());
                }

            }
        }

        return new ArrayList<>(result);
    }

    private static Map<Long, SysPermissionTreeNode> getTreeNodeMap(List<SysPermission> permissions) {
        Map<Long, SysPermissionTreeNode> nodeMap = new LinkedHashMap<>();

        for (SysPermission p : permissions) {
            SysPermissionTreeNode node = new SysPermissionTreeNode();
            BeanUtils.copyProperties(p, node);

            nodeMap.put(p.getId(), node);
        }
        return nodeMap;
    }
}
