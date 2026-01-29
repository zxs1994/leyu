package com.xusheng94.leyu.controller;

import com.xusheng94.leyu.common.BaseQuery;
import com.xusheng94.leyu.common.BizException;
import com.xusheng94.leyu.model.dto.SysRoleDto;
import com.xusheng94.leyu.entity.SysRole;
import com.xusheng94.leyu.service.ISysPermissionService;
import com.xusheng94.leyu.service.ISysRoleService;
import com.xusheng94.leyu.model.vo.SysPermissionTreeNode;
import com.xusheng94.leyu.model.vo.SysRoleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/role")
@Tag(name = "系统--角色", description = "系统--角色控制器")
public class SysRoleController {

    private final ISysRoleService sysRoleService;
    private final ISysPermissionService sysPermissionService;

    @GetMapping("/page")
    @Operation(summary = "角色列表(分页)")
    public Page<SysRoleVo> page(BaseQuery query) {
        return sysRoleService.page(query);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取角色")
    public SysRole item(@PathVariable Long id) {
        SysRole sysRole = sysRoleService.getById(id);
        if (sysRole == null) {
            throw new BizException(404, "角色未找到");
        }
        return sysRole;
    }

    @PostMapping
    @Operation(summary = "新增角色")
    public Long add(@RequestBody SysRoleDto dto) {

        Long id = sysRoleService.save(dto);
        if (id == null) {
            throw new BizException(400, "新增角色失败");
        }
        return id;
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新角色")
    public void update(@PathVariable Long id, @RequestBody SysRoleDto dto) {
        dto.setId(id);
        boolean success = sysRoleService.updateById(dto);
        if (!success) {
            throw new BizException(400, "更新角色失败");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    public void delete(@PathVariable Long id) {
        boolean success = sysRoleService.removeById(id);
        if (!success) {
            throw new BizException(400, "删除角色失败");
        }
    }

    @GetMapping
    @Operation(summary = "角色列表")
    public List<SysRole> list() {
        return sysRoleService.list();
    }

    @GetMapping("/permission-tree")
    @Operation(summary = "权限树形数据")
    public List<SysPermissionTreeNode> permissionTree() {
        return sysPermissionService.getTree();
    }
}