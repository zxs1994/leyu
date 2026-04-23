package com.xusheng94.leyu.admin.controller;

import com.xusheng94.leyu.common.BizException;
import com.xusheng94.leyu.admin.service.ITestDataScopeService;
import com.xusheng94.leyu.admin.model.query.TestDataScopeQuery;
import com.xusheng94.leyu.admin.model.dto.TestDataScopeDto;
import com.xusheng94.leyu.admin.model.vo.TestDataScopeVo;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * <p>
 * 测试--数据权限表 Controller 控制器
 * </p>
 *
 * @author xusheng
 * @since 2026-04-23 14:31:17
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/test/data-scope")
@Tag(name = "测试--数据权限", description = "测试--数据权限控制器")
public class TestDataScopeController {

    private static final String TITLE = "数据权限";
    private final ITestDataScopeService testDataScopeService;

    @GetMapping("/page")
    @Operation(summary = TITLE + "列表(分页)")
    public Page<TestDataScopeVo> page(TestDataScopeQuery query) {
        return testDataScopeService.page(query);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取" + TITLE)
    public TestDataScopeVo item(@PathVariable Long id) {
        TestDataScopeVo vo = testDataScopeService.getVoById(id);
        if (vo == null) {
            throw new BizException(404, TITLE + "未找到");
        }
        return vo;
    }

    @PostMapping
    @Operation(summary = "新增" + TITLE)
    public Long add(@RequestBody TestDataScopeDto dto) {
        boolean success = testDataScopeService.save(dto);
        if (!success) {
            throw new BizException(400, "新增" + TITLE + "失败");
        }
        return dto.getId();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新" + TITLE)
    public void update(@PathVariable Long id, @RequestBody TestDataScopeDto dto) {
        dto.setId(id);
        boolean success = testDataScopeService.updateById(dto);
        if (!success) {
            throw new BizException(400, "更新" + TITLE + "失败");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除" + TITLE)
    public void delete(@PathVariable Long id) {
        boolean success = testDataScopeService.removeById(id);
        if (!success) {
            throw new BizException(400, "删除" + TITLE + "失败");
        }
    }

//    @GetMapping
//    @Operation(summary = TITLE + "列表")
//    public List<TestDataScope> list() {
//        return testDataScopeService.list();
//    }

}