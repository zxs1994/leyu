package com.xusheng94.leyu.controller;

import com.xusheng94.leyu.common.BizException;
import com.xusheng94.leyu.entity.SysDept;
import com.xusheng94.leyu.model.query.SysDeptQuery;
import com.xusheng94.leyu.service.ISysDeptService;
import com.xusheng94.leyu.model.vo.SysDeptTreeNode;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * <p>
 * 系统--组织部门表 Controller 控制器
 * </p>
 *
 * @author xusheng
 * @since 2026-01-20 14:06:31
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/dept")
@Tag(name = "系统--组织部门", description = "系统--组织部门控制器")
public class SysDeptController {

    private final ISysDeptService sysDeptService;

    @GetMapping("/page")
    @Operation(summary = "组织部门列表(分页)")
    public Page<SysDept> page(SysDeptQuery query) {
        return sysDeptService.page(query);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取组织部门")
    public SysDept item(@PathVariable Long id) {
        SysDept sysDept = sysDeptService.getById(id);
        if (sysDept == null) {
            throw new BizException(404, "组织部门未找到");
        }
        return sysDept;
    }

    @PostMapping
    @Operation(summary = "新增组织部门")
    public Long add(@RequestBody SysDept sysDept) {
        boolean success = sysDeptService.save(sysDept);
        if (!success) {
            throw new BizException(400, "新增组织部门失败");
        }
        return sysDept.getId();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新组织部门")
    public void update(@PathVariable Long id, @RequestBody SysDept sysDept) {
        sysDept.setId(id);
        boolean success = sysDeptService.updateById(sysDept);
        if (!success) {
            throw new BizException(400, "更新组织部门失败");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除组织部门")
    public void delete(@PathVariable Long id) {
        boolean success = sysDeptService.removeById(id);
        if (!success) {
            throw new BizException(400, "删除组织部门失败");
        }
    }

    @GetMapping
    @Operation(summary = "组织部门列表")
    public List<SysDept> list() {
        return sysDeptService.list();
    }

    @GetMapping("/tree")
    @Operation(summary = "组织部门树形数据")
    public List<SysDeptTreeNode> tree() {
        return sysDeptService.getTree();
    }

}