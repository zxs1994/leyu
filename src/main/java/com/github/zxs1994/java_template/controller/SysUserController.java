package com.github.zxs1994.java_template.controller;

import com.github.zxs1994.java_template.common.BizException;
import com.github.zxs1994.java_template.entity.SysUser;
import com.github.zxs1994.java_template.enums.DataSourceType;
import com.github.zxs1994.java_template.service.ISysUserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 系统--用户表 Controller 控制器
 * </p>
 *
 * @author xusheng
 * @since 2026-01-10 01:41:52
 */

@RestController
@RequestMapping("/sys/user")
@Tag(name = "系统--用户", description = "系统--用户控制器")
public class SysUserController {

    @Autowired
    private ISysUserService sysUserService;

    @GetMapping
    @Operation(summary = "用户列表")
    public List<SysUser> list() {
        return sysUserService.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取用户")
    public SysUser get(@PathVariable Long id) {
        SysUser entityLower = sysUserService.getById(id);
        if (entityLower == null) {
            throw new BizException(404, "用户未找到");
        }
        return entityLower;
    }

    @PostMapping
    @Operation(summary = "新增用户")
    public SysUser save(@RequestBody SysUser sysUser) {
        sysUser.setTokenVersion(0);
        sysUser.setSource(DataSourceType.USER.getCode());
        boolean success = sysUserService.save(sysUser);
        if (!success) {
            throw new BizException(400, "新增用户失败");
        }
        return sysUser;
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户")
    public SysUser update(@PathVariable Long id, @RequestBody SysUser sysUser) {
        sysUser.setId(id);
        sysUser.setTokenVersion(null);
        boolean success = sysUserService.updateById(sysUser);
        if (!success) {
            throw new BizException(400, "更新用户失败");
        }
        return sysUser;
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    public void delete(@PathVariable Long id) {
        boolean success = sysUserService.removeById(id);
        if (!success) {
            throw new BizException(400, "删除用户失败");
        }
    }

    @GetMapping("/page")
    @Operation(summary = "用户列表(分页)")
    public Page<SysUser> page(@RequestParam(defaultValue = "1") long page,
                                 @RequestParam(defaultValue = "10") long size) {
        return sysUserService.page(new Page<>(page, size));
    }
}