package com.example.template.controller;

import com.example.template.entity.User;
import com.example.template.service.IUserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * UserController 控制器
 * </p>
 *
 * @author xusheng
 * @since 2026-01-02 16:00:32
 */

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "User 控制器")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping
    @Operation(summary = "获取所有 User 列表")
    public List<User> list() {
        return userService.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 获取 User")
    public User get(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    @Operation(summary = "新增 User")
    public boolean save(@RequestBody User user) {
        return userService.save(user);
    }

    @PutMapping
    @Operation(summary = "更新 User")
    public boolean update(@RequestBody User user) {
        return userService.updateById(user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除 User 根据 ID")
    public boolean delete(@PathVariable Long id) {
        return userService.removeById(id);
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取 User 列表")
    public Page<User> page(@RequestParam(defaultValue = "1") long page,
                                 @RequestParam(defaultValue = "10") long size) {
        return userService.page(new Page<>(page, size));
    }
}