package com.xusheng94.leyu.admin.controller;

import com.xusheng94.leyu.admin.entity.SysPermission;
import com.xusheng94.leyu.admin.service.ISysPermissionService;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.StringUtils;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * <p>
 * 系统--权限表 Controller 控制器
 * </p>
 *
 * @author xusheng
 * @since 2026-01-12 17:10:10
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/permission")
@Tag(name = "系统--权限", description = "系统--权限控制器")
public class SysPermissionController {

    private final ISysPermissionService sysPermissionService;

    @GetMapping("/page")
    @Operation(summary = "权限列表(分页)")
    public Page<SysPermission> page(@RequestParam(defaultValue = "1") long page,
                                    @RequestParam(defaultValue = "10") long size,
                                    @RequestParam(required = false) String name) {
        QueryWrapper<SysPermission> qw = new QueryWrapper<>();
        if (StringUtils.hasText(name)) {
            qw.like("name", name);
        }
        return sysPermissionService.page(new Page<>(page, size), qw);
    }

//    @GetMapping("/{id}")
//    @Operation(summary = "获取权限")
//    public SysPermission item(@PathVariable Long id) {
//        SysPermission sysPermission = sysPermissionService.getById(id);
//        if (sysPermission == null) {
//            throw new BizException(404, "权限未找到");
//        }
//        return sysPermission;
//    }

//    @GetMapping
//    @Operation(summary = "权限列表")
//    public List<SysPermission> list() {
//        return sysPermissionService.list();
//    }

//    @GetMapping("/tree")
//    @Operation(summary = "权限树形数据")
//    public List<SysPermissionTreeNode> tree() {
//        return sysPermissionService.getPermissionTree();
//    }

}