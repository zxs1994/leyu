package com.xusheng94.leyu.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xusheng94.leyu.admin.entity.SysRole;
import com.xusheng94.leyu.admin.entity.SysUserRole;
import com.xusheng94.leyu.admin.mapper.SysRoleMapper;
import com.xusheng94.leyu.admin.mapper.SysUserRoleMapper;
import com.xusheng94.leyu.admin.service.ISysUserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统--用户-角色关联表 服务实现类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-10 01:41:52
 */
@Service
@RequiredArgsConstructor
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleMapper sysRoleMapper;

    @Override
    public Map<Long, List<SysRole>> getRolesByUserIds(List<Long> userIds) {
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, userIds)
        );

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .distinct()
                .collect(Collectors.toList());

        List<SysRole> roles = sysRoleMapper.selectBatchIds(roleIds);

        Map<Long, List<SysRole>> map = new HashMap<>();
        for (SysUserRole ur : userRoles) {
            SysRole role = roles.stream()
                    .filter(r -> r.getId().equals(ur.getRoleId()))
                    .findFirst().orElse(null);
            map.computeIfAbsent(ur.getUserId(), k -> new ArrayList<>());
            if (role != null) {
                map.get(ur.getUserId()).add(role);
            }
        }
        return map;
    }
}
