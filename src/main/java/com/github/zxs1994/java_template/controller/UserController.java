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
 * 用户表 Controller 控制器
 * </p>
 *
 * @author xusheng
 * @since 2026-01-04 17:53:21
 */

@RestController
@RequestMapping("/user")
@Tag(name = "用户", description = "用户 控制器")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping
    @Operation(summary = "获取所有 用户 列表")
    public List<User> list() {
        return userService.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 获取 用户")
    public User get(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    @Operation(summary = "新增 用户")
    public boolean save(@RequestBody User user) {
        return userService.save(user);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 用户")
    public boolean update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        user.setPassword(null);
        return userService.updateById(user);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除 用户 根据 ID")
    public boolean delete(@PathVariable Long id) {
        return userService.removeById(id);
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取 用户 列表")
    public Page<User> page(@RequestParam(defaultValue = "1") long page,
                                   @RequestParam(defaultValue = "10") long size) {
        return userService.page(new Page<>(page, size));
    }

    @PostMapping("/register")
    @Operation(summary = "客户端 新增 用户")
    public boolean register(@RequestBody User user) {
        return userService.save(user);
    }

}