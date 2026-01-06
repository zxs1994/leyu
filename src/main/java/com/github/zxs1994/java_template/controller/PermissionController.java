package com.example.template.controller;

import com.example.template.entity.Permission;
import com.example.template.service.IPermissionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 权限表 Controller 控制器
 * </p>
 *
 * @author xusheng
 * @since 2026-01-06 09:59:00
 */

@RestController
@RequestMapping("/permission")
@Tag(name = "权限", description = "权限 控制器")
public class PermissionController {

    @Autowired
    private IPermissionService permissionService;

    @GetMapping
    @Operation(summary = "获取所有 权限 列表")
    public List<Permission> list() {
        return permissionService.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 获取 权限")
    public Permission get(@PathVariable Long id) {
        return permissionService.getById(id);
    }

    @PostMapping
    @Operation(summary = "新增 权限")
    public boolean save(@RequestBody Permission permission) {
        return permissionService.save(permission);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 权限")
    public boolean update(@PathVariable Long id, @RequestBody Permission permission) {
        permission.setId(id);
        return permissionService.updateById(permission);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除 权限 根据 ID")
    public boolean delete(@PathVariable Long id) {
        return permissionService.removeById(id);
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取 权限 列表")
    public Page<Permission> page(@RequestParam(defaultValue = "1") long page,
                                 @RequestParam(defaultValue = "10") long size) {
        return permissionService.page(new Page<>(page, size));
    }
}