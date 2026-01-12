package com.github.zxs1994.java_template.controller;

import com.github.zxs1994.java_template.common.BizException;
import com.github.zxs1994.java_template.entity.SysRolePermission;
import com.github.zxs1994.java_template.service.ISysRolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 系统--角色-权限关联表 Controller 控制器
 * </p>
 *
 * @author xusheng
 * @since 2026-01-10 01:41:52
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/role-permission")
@Tag(name = "系统--角色-权限关联", description = "系统--角色-权限关联控制器")
public class SysRolePermissionController {

    private final ISysRolePermissionService sysRolePermissionService;

    @GetMapping
    @Operation(summary = "角色-权限关联列表")
    public List<SysRolePermission> list() {
        return sysRolePermissionService.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取角色-权限关联")
    public SysRolePermission get(@PathVariable Long id) {
        SysRolePermission entityLower = sysRolePermissionService.getById(id);
        if (entityLower == null) {
            throw new BizException(404, "角色-权限关联未找到");
        }
        return entityLower;
    }

    @PostMapping
    @Operation(summary = "新增角色-权限关联")
    public SysRolePermission save(@RequestBody SysRolePermission sysRolePermission) {
        boolean success = sysRolePermissionService.save(sysRolePermission);
        if (!success) {
            throw new BizException(400, "新增角色-权限关联失败");
        }
        return sysRolePermission;
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色-权限关联")
    public SysRolePermission update(@PathVariable Long id, @RequestBody SysRolePermission sysRolePermission) {
        sysRolePermission.setId(id);
        boolean success = sysRolePermissionService.updateById(sysRolePermission);
        if (!success) {
            throw new BizException(400, "更新角色-权限关联失败");
        }
        return sysRolePermission;
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色-权限关联")
    public void delete(@PathVariable Long id) {
        boolean success = sysRolePermissionService.removeById(id);
        if (!success) {
            throw new BizException(400, "删除角色-权限关联失败");
        }
    }

    @GetMapping("/page")
    @Operation(summary = "角色-权限关联列表(分页)")
    public Page<SysRolePermission> page(@RequestParam(defaultValue = "1") long page,
                                 @RequestParam(defaultValue = "10") long size) {
        return sysRolePermissionService.page(new Page<>(page, size));
    }
}