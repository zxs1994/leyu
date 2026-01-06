package com.example.template.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 通用 CRUD Controller（备用）
 * </p>
 *
 * @param <T> 实体类型
 * @param <S> Service 类型
 *
 * @author xusheng
 */
public abstract class BaseCrudController<T, S extends IService<T>> {

    @Autowired
    protected S service;

    @GetMapping
    @Operation(summary = "获取全部列表")
    public List<T> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 获取详情")
    public T getById(@PathVariable Serializable id) {
        return service.getById(id);
    }

    @PostMapping
    @Operation(summary = "新增")
    public boolean save(@RequestBody T entity) {
        return service.save(entity);
    }

    @PutMapping
    @Operation(summary = "更新")
    public boolean update(@RequestBody T entity) {
        return service.updateById(entity);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据 ID 删除")
    public boolean delete(@PathVariable Serializable id) {
        return service.removeById(id);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public Page<T> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size
    ) {
        return service.page(new Page<>(page, size));
    }
}