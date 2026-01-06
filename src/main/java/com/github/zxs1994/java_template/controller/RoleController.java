package com.example.template.controller;

import com.example.template.entity.Role;
import com.example.template.service.IRoleService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 角色表 Controller 控制器
 * </p>
 *
 * @author xusheng
 * @since 2026-01-06 09:59:01
 */

@RestController
@RequestMapping("/role")
@Tag(name = "角色", description = "角色 控制器")
public class RoleController {

    @Autowired
    private IRoleService roleService;

    @GetMapping
    @Operation(summary = "获取所有 角色 列表")
    public List<Role> list() {
        return roleService.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 获取 角色")
    public Role get(@PathVariable Long id) {
        return roleService.getById(id);
    }

    @PostMapping
    @Operation(summary = "新增 角色")
    public boolean save(@RequestBody Role role) {
        return roleService.save(role);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 角色")
    public boolean update(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        return roleService.updateById(role);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除 角色 根据 ID")
    public boolean delete(@PathVariable Long id) {
        return roleService.removeById(id);
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取 角色 列表")
    public Page<Role> page(@RequestParam(defaultValue = "1") long page,
                                 @RequestParam(defaultValue = "10") long size) {
        return roleService.page(new Page<>(page, size));
    }
}