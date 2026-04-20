package com.xusheng94.leyu.admin.controller;

import com.xusheng94.leyu.common.BizException;
import com.xusheng94.leyu.admin.service.IDataScopeTestService;
import com.xusheng94.leyu.admin.model.query.DataScopeTestQuery;
import com.xusheng94.leyu.admin.model.dto.DataScopeTestDto;
import com.xusheng94.leyu.admin.model.vo.DataScopeTestVo;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * <p>
 * 数据权限测试表 Controller 控制器
 * </p>
 *
 * @author xusheng
 * @since 2026-04-17 18:20:39
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/data-scope-test")
@Tag(name = "数据权限测试", description = "数据权限测试控制器")
public class DataScopeTestController {

    private static final String TITLE = "数据权限测试";
    private final IDataScopeTestService dataScopeTestService;

    @GetMapping("/page")
    @Operation(summary = TITLE + "列表(分页)")
    public Page<DataScopeTestVo> page(DataScopeTestQuery query) {
        return dataScopeTestService.page(query);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取" + TITLE)
    public DataScopeTestVo item(@PathVariable Long id) {
        DataScopeTestVo vo = dataScopeTestService.getVoById(id);
        if (vo == null) {
            throw new BizException(404, TITLE + "未找到");
        }
        return vo;
    }

    @PostMapping
    @Operation(summary = "新增" + TITLE)
    public Long add(@RequestBody DataScopeTestDto dto) {
        boolean success = dataScopeTestService.save(dto);
        if (!success) {
            throw new BizException(400, "新增" + TITLE + "失败");
        }
        return dto.getId();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新" + TITLE)
    public void update(@PathVariable Long id, @RequestBody DataScopeTestDto dto) {
        dto.setId(id);
        boolean success = dataScopeTestService.updateById(dto);
        if (!success) {
            throw new BizException(400, "更新" + TITLE + "失败");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除" + TITLE)
    public void delete(@PathVariable Long id) {
        boolean success = dataScopeTestService.removeById(id);
        if (!success) {
            throw new BizException(400, "删除" + TITLE + "失败");
        }
    }

//    @GetMapping
//    @Operation(summary = TITLE + "列表")
//    public List<DataScopeTest> list() {
//        return dataScopeTestService.list();
//    }

}