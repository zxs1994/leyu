package com.github.zxs1994.java_template.controller;

import com.github.zxs1994.java_template.common.BizException;
import com.github.zxs1994.java_template.entity.SysRole;
import com.github.zxs1994.java_template.enums.DataSourceType;
import com.github.zxs1994.java_template.service.ISysRoleService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 系统--角色表 Controller 控制器
 * </p>
 *
 * @author xusheng
 * @since 2026-01-10 01:41:52
 */

@RestController
@RequestMapping("/sys/role")
@Tag(name = "系统--角色", description = "系统--角色控制器")
public class SysRoleController {

    @Autowired
    private ISysRoleService sysRoleService;

    @GetMapping
    @Operation(summary = "角色列表")
    public List<SysRole> list() {
        return sysRoleService.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取角色")
    public SysRole get(@PathVariable Long id) {
        SysRole entityLower = sysRoleService.getById(id);
        if (entityLower == null) {
            throw new BizException(404, "角色未找到");
        }
        return entityLower;
    }

    @PostMapping
    @Operation(summary = "新增角色")
    public SysRole save(@RequestBody SysRole sysRole) {
        sysRole.setSource(DataSourceType.USER.getCode());
        boolean success = sysRoleService.save(sysRole);
        if (!success) {
            throw new BizException(400, "新增角色失败");
        }
        return sysRole;
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色")
    public SysRole update(@PathVariable Long id, @RequestBody SysRole sysRole) {
        sysRole.setId(id);
        boolean success = sysRoleService.updateById(sysRole);
        if (!success) {
            throw new BizException(400, "更新角色失败");
        }
        return sysRole;
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    public void delete(@PathVariable Long id) {
        boolean success = sysRoleService.removeById(id);
        if (!success) {
            throw new BizException(400, "删除用户失败");
        }
    }

    @GetMapping("/page")
    @Operation(summary = "角色列表(分页)")
    public Page<SysRole> page(@RequestParam(defaultValue = "1") long page,
                                 @RequestParam(defaultValue = "10") long size) {
        return sysRoleService.page(new Page<>(page, size));
    }
}