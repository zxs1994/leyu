package com.xusheng94.leyu.admin.controller;

import com.xusheng94.leyu.admin.model.dto.TenantDto;
import com.xusheng94.leyu.admin.entity.SysDept;
import com.xusheng94.leyu.admin.service.IPlatformTenantService;
import com.xusheng94.leyu.admin.model.vo.LoginVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 平台--租户接口
 * </p>
 *
 * @author xusheng
 * @since 2026-01-22
 */

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/platform/tenant")
@Tag(name = "平台--租户", description = "平台--租户控制器")
public class PlatformTenantController {

    private final IPlatformTenantService platformTenantService;

    @PostMapping
    @Operation(summary = "新增租户")
    @Transactional
    public Long add(@RequestBody TenantDto dto) {
        return platformTenantService.addTenantWithAdmin(dto);
    }

    @GetMapping
    @Operation(summary = "获取租户列表")
    public List<SysDept> list() {
        return platformTenantService.list();
    }

    @PostMapping("/switch/{id}")
    @Operation(summary = "切换到目标租户")
    // 对外接口路径追求简洁，对内变量名追求明确
    public LoginVo switchTo(@PathVariable("id") Long tenantId) {
        return platformTenantService.switchTo(tenantId);
    }
}
