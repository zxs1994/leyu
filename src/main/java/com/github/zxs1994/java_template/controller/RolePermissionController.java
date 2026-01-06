package com.example.template.controller;

import com.example.template.entity.RolePermission;
import com.example.template.service.IRolePermissionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 角色-权限关联表 Controller 控制器
 * </p>
 *
 * @author xusheng
 * @since 2026-01-06 09:59:01
 */

@RestController
@RequestMapping("/rolePermission")
@Tag(name = "角色-权限关联", description = "角色-权限关联 控制器")
public class RolePermissionController {

    @Autowired
    private IRolePermissionService rolePermissionService;

    @GetMapping
    @Operation(summary = "获取所有 角色-权限关联 列表")
    public List<RolePermission> list() {
        return rolePermissionService.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 获取 角色-权限关联")
    public RolePermission get(@PathVariable Long id) {
        return rolePermissionService.getById(id);
    }

    @PostMapping
    @Operation(summary = "新增 角色-权限关联")
    public boolean save(@RequestBody RolePermission rolePermission) {
        return rolePermissionService.save(rolePermission);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 角色-权限关联")
    public boolean update(@PathVariable Long id, @RequestBody RolePermission rolePermission) {
        rolePermission.setId(id);
        return rolePermissionService.updateById(rolePermission);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除 角色-权限关联 根据 ID")
    public boolean delete(@PathVariable Long id) {
        return rolePermissionService.removeById(id);
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取 角色-权限关联 列表")
    public Page<RolePermission> page(@RequestParam(defaultValue = "1") long page,
                                 @RequestParam(defaultValue = "10") long size) {
        return rolePermissionService.page(new Page<>(page, size));
    }
}