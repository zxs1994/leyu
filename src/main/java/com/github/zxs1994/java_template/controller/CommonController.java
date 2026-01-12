package com.github.zxs1994.java_template.controller;

import com.github.zxs1994.java_template.config.ProjectProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.github.zxs1994.java_template.util.EnumUtils;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 公共接口
 * </p>
 *
 * @author xusheng
 * @since 2026-01-05
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/common")
@Tag(name = "公共", description = "公共控制器")
public class CommonController {

    private final ProjectProperties projectProperties;

    @GetMapping("/enums")
    @Operation(summary = "获取所有枚举")
    public Map<String, List<Map<String, Object>>> list() {
        return EnumUtils.loadAllEnums(projectProperties.getBasePackage() + ".enums");
    }
}
