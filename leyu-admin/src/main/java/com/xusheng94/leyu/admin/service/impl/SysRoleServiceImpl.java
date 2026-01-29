package com.xusheng94.leyu.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xusheng94.leyu.admin.common.BaseQuery;
import com.xusheng94.leyu.admin.common.BizException;
import com.xusheng94.leyu.admin.model.dto.SysRoleDto;
import com.xusheng94.leyu.admin.entity.SysPermission;
import com.xusheng94.leyu.admin.entity.SysRole;

import com.xusheng94.leyu.admin.entity.SysRolePermission;
import com.xusheng94.leyu.admin.common.enums.SourceType;
import com.xusheng94.leyu.admin.mapper.SysRoleMapper;
import com.xusheng94.leyu.admin.service.ISysPermissionService;
import com.xusheng94.leyu.admin.service.ISysRolePermissionService;
import com.xusheng94.leyu.admin.service.ISysRoleService;
import com.xusheng94.leyu.admin.common.util.CurrentUser;
import com.xusheng94.leyu.admin.common.util.TenantQueryHelper;
import com.xusheng94.leyu.admin.model.vo.SysRoleVo;
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
 * 系统--角色表 服务实现类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-10 01:41:52
 */
@RequiredArgsConstructor
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    private final ISysRolePermissionService sysRolePermissionService;
    private final ISysPermissionService sysPermissionService;

    @Override
    public List<SysRole> list() {

        QueryWrapper<SysRole> qw = new QueryWrapper<>();
        TenantQueryHelper.tenantOrSystem(qw);

        return super.list(qw);
    }

    @Override
    @Transactional
    public boolean removeById(Long id) {
        SysRole sysRole;
        try {
            sysRole = getById(id); // 已做租户 + 存在性校验
        } catch (BizException e) {
            throw new BizException(403, "角色不属于当前租户!");
        }

        if (SourceType.SYSTEM.getCode().equals(sysRole.getSource())) {
            throw new BizException(403, "系统内置角色不能删除!");
        }

        return super.removeById(id);
    }

    @Override
    public SysRole getById(Long id) {
        QueryWrapper<SysRole> qw = new QueryWrapper<>();
        qw.eq("id", id);
        TenantQueryHelper.tenantOnly(qw);

        SysRole sysRole = super.getOne(qw);
        if (sysRole == null) {
            throw new BizException(404, "角色未找到");
        }
        return sysRole;
    }

    @Override
    public Page<SysRoleVo> page(BaseQuery query) {
        // 1️⃣ 分页查询角色
        QueryWrapper<SysRole> qw = new QueryWrapper<>();

        TenantQueryHelper.tenantOrSystem(qw);

        if (StringUtils.hasText(query.getName())) {
            qw.like("name", query.getName());
        }

        Page<SysRole> rolePage = super.page(new Page<>(query.getPage(), query.getSize()), qw);
        List<SysRole> roles = rolePage.getRecords();

        if (roles.isEmpty()) {
            return new Page<SysRoleVo>()
                    .setRecords(List.of())
                    .setTotal(0);
        }

        // 2️⃣ 角色ID集合
        List<Long> roleIds = roles.stream()
                .map(SysRole::getId)
                .toList();

        // 3️⃣ 查询角色-权限关联
        List<SysRolePermission> rolePermissions =
                sysRolePermissionService.list(
                        new QueryWrapper<SysRolePermission>()
                                .in("role_id", roleIds)
                );

        // 4️⃣ 权限ID集合
        List<Long> permissionIds = rolePermissions.stream()
                .map(SysRolePermission::getPermissionId)
                .distinct()
                .toList();

        // 5️⃣ 查询权限
        List<SysPermission> permissions =
                permissionIds.isEmpty()
                        ? List.of()
                        : sysPermissionService.listByIds(permissionIds);

        // 6️⃣ 构建 Map（核心优化点）
        Map<Long, SysPermission> permissionMap = permissions.stream()
                .collect(Collectors.toMap(SysPermission::getId, p -> p));

        Map<Long, List<SysRolePermission>> rolePermissionMap =
                rolePermissions.stream()
                        .collect(Collectors.groupingBy(SysRolePermission::getRoleId));

        // 7️⃣ 组装 VO
        List<SysRoleVo> roleVos = roles.stream()
                .map(role -> {
                    List<SysPermission> rolePerms =
                            rolePermissionMap
                                    .getOrDefault(role.getId(), List.of())
                                    .stream()
                                    .map(rp -> permissionMap.get(rp.getPermissionId()))
                                    .filter(Objects::nonNull)
                                    .toList();

                    return new SysRoleVo(role, rolePerms);
                })
                .toList();

        // 8️⃣ 返回分页结果
        Page<SysRoleVo> voPage = new Page<>();

        BeanUtils.copyProperties(rolePage, voPage, "records");
        voPage.setRecords(roleVos);

        return voPage;
    }

    @Transactional
    @Override
    public boolean updateById(SysRoleDto dto) {
        Long roleId = dto.getId();

        SysRole oldRole;

        try {
            oldRole = getById(roleId); // 已做租户 + 存在性校验
        } catch (BizException e) {
            throw new BizException(403, "角色不属于当前租户!");
        }

        if (SourceType.SYSTEM.getCode().equals(oldRole.getSource())) {
            throw new BizException(403, "系统内置角色不能更新!");
        }

        SysRole sysRole = new SysRole();

        BeanUtils.copyProperties(dto, sysRole);
        updateById(sysRole);
        sysRolePermissionService.remove(
                new LambdaQueryWrapper<SysRolePermission>()
                        .eq(SysRolePermission::getRoleId, roleId)
        );
        saveRolePermissions(roleId, dto.getPermissionIds());
        return true;
    }

    @Transactional
    @Override
    public Long save(SysRoleDto dto) {

        if (dto.getCode() == null || dto.getCode().isEmpty()) {
            dto.setCode("ROLE_" + System.currentTimeMillis());
        }

        SysRole sysRole = new SysRole();

        BeanUtils.copyProperties(dto, sysRole);

        sysRole.setTenantId(CurrentUser.getTenantId());

        super.save(sysRole);

        saveRolePermissions(sysRole.getId(), dto.getPermissionIds());

        return sysRole.getId();
    }

    /**
     * 给角色批量更新权限关联
     *
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     */
    private void saveRolePermissions(Long roleId, List<Long> permissionIds) {
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<SysRolePermission> list = permissionIds.stream()
                    .map(permissionId -> {
                        SysRolePermission sysRolePermission = new SysRolePermission();
                        sysRolePermission.setRoleId(roleId);
                        sysRolePermission.setPermissionId(permissionId);
                        return sysRolePermission;
                    })
                    .collect(Collectors.toList());
            sysRolePermissionService.saveBatch(list);
        }
    }
}
