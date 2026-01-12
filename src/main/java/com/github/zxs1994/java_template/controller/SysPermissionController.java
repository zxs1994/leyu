package com.github.zxs1994.java_template.controller;

import com.github.zxs1994.java_template.common.BizException;
import com.github.zxs1994.java_template.dto.PermissionTreeNode;
import com.github.zxs1994.java_template.entity.SysPermission;
import com.github.zxs1994.java_template.service.ISysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * <p>
 * 系统--权限表 Controller 控制器
 * </p>
 *
 * @author xusheng
 * @since 2026-01-10 01:41:52
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/permission")
@Tag(name = "系统--权限", description = "系统--权限控制器")
public class SysPermissionController {

    private final ISysPermissionService sysPermissionService;

    @GetMapping
    @Operation(summary = "权限列表")
    public List<SysPermission> list() {
        return sysPermissionService.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取权限")
    public SysPermission get(@PathVariable Long id) {
        SysPermission entityLower = sysPermissionService.getById(id);
        if (entityLower == null) {
            throw new BizException(404, "权限未找到");
        }
        return entityLower;
    }

    @PostMapping
    @Operation(summary = "新增权限")
    public SysPermission save(@RequestBody SysPermission sysPermission) {
        boolean success = sysPermissionService.save(sysPermission);
        if (!success) {
            throw new BizException(400, "新增权限失败");
        }
        return sysPermission;
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新权限")
    public SysPermission update(@PathVariable Long id, @RequestBody SysPermission sysPermission) {
        sysPermission.setId(id);
        boolean success = sysPermissionService.updateById(sysPermission);
        if (!success) {
            throw new BizException(400, "更新权限失败");
        }
        return sysPermission;
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限")
    public void delete(@PathVariable Long id) {
        boolean success = sysPermissionService.removeById(id);
        if (!success) {
            throw new BizException(400, "删除权限失败");
        }
    }

    @GetMapping("/page")
    @Operation(summary = "权限列表(分页)")
    public Page<SysPermission> page(@RequestParam(defaultValue = "1") long page,
                                 @RequestParam(defaultValue = "10") long size) {
        return sysPermissionService.page(new Page<>(page, size));
    }

    @GetMapping("/tree")
    @Operation(summary = "获取权限树")
    public List<PermissionTreeNode> tree() {
        return sysPermissionService.getPermissionTree();
    }
}