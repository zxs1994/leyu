package com.xusheng94.leyu.admin.controller;

import com.xusheng94.leyu.common.BizException;
import com.xusheng94.leyu.admin.service.ISysOperationLogService;
import com.xusheng94.leyu.admin.model.query.SysOperationLogQuery;
import com.xusheng94.leyu.admin.model.dto.SysOperationLogDto;
import com.xusheng94.leyu.admin.model.vo.SysOperationLogVo;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * <p>
 * 系统--操作日志表 Controller 控制器
 * </p>
 *
 * @author xusheng
 * @since 2026-04-23 15:14:17
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/sys/operation-log")
@Tag(name = "系统--操作日志", description = "系统--操作日志控制器")
public class SysOperationLogController {

    private static final String TITLE = "操作日志";
    private final ISysOperationLogService sysOperationLogService;

    @GetMapping("/page")
    @Operation(summary = TITLE + "列表(分页)")
    public Page<SysOperationLogVo> page(SysOperationLogQuery query) {
        return sysOperationLogService.page(query);
    }

    // @GetMapping("/{id}")
    // @Operation(summary = "获取" + TITLE)
    // public SysOperationLogVo item(@PathVariable Long id) {
    // SysOperationLogVo vo = sysOperationLogService.getVoById(id);
    // if (vo == null) {
    // throw new BizException(404, TITLE + "未找到");
    // }
    // return vo;
    // }

    // @PostMapping
    // @Operation(summary = "新增" + TITLE)
    // public Long add(@RequestBody SysOperationLogDto dto) {
    // boolean success = sysOperationLogService.save(dto);
    // if (!success) {
    // throw new BizException(400, "新增" + TITLE + "失败");
    // }
    // return dto.getId();
    // }
    //
    // @PutMapping("/{id}")
    // @Operation(summary = "更新" + TITLE)
    // public void update(@PathVariable Long id, @RequestBody SysOperationLogDto
    // dto) {
    // dto.setId(id);
    // boolean success = sysOperationLogService.updateById(dto);
    // if (!success) {
    // throw new BizException(400, "更新" + TITLE + "失败");
    // }
    // }
    //
    // @DeleteMapping("/{id}")
    // @Operation(summary = "删除" + TITLE)
    // public void delete(@PathVariable Long id) {
    // boolean success = sysOperationLogService.removeById(id);
    // if (!success) {
    // throw new BizException(400, "删除" + TITLE + "失败");
    // }
    // }

    // @GetMapping
    // @Operation(summary = TITLE + "列表")
    // public List<SysOperationLog> list() {
    // return sysOperationLogService.list();
    // }

}