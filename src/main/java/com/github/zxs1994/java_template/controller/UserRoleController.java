package com.example.template.controller;

import com.example.template.entity.UserRole;
import com.example.template.service.IUserRoleService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 用户-角色关联表 Controller 控制器
 * </p>
 *
 * @author xusheng
 * @since 2026-01-06 09:59:01
 */

@RestController
@RequestMapping("/userRole")
@Tag(name = "用户-角色关联", description = "用户-角色关联 控制器")
public class UserRoleController {

    @Autowired
    private IUserRoleService userRoleService;

    @GetMapping
    @Operation(summary = "获取所有 用户-角色关联 列表")
    public List<UserRole> list() {
        return userRoleService.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 获取 用户-角色关联")
    public UserRole get(@PathVariable Long id) {
        return userRoleService.getById(id);
    }

    @PostMapping
    @Operation(summary = "新增 用户-角色关联")
    public boolean save(@RequestBody UserRole userRole) {
        return userRoleService.save(userRole);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 用户-角色关联")
    public boolean update(@PathVariable Long id, @RequestBody UserRole userRole) {
        userRole.setId(id);
        return userRoleService.updateById(userRole);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除 用户-角色关联 根据 ID")
    public boolean delete(@PathVariable Long id) {
        return userRoleService.removeById(id);
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取 用户-角色关联 列表")
    public Page<UserRole> page(@RequestParam(defaultValue = "1") long page,
                                 @RequestParam(defaultValue = "10") long size) {
        return userRoleService.page(new Page<>(page, size));
    }
}