package com.github.zxs1994.java_template.controller;

import com.github.zxs1994.java_template.dto.LoginRequest;
import com.github.zxs1994.java_template.dto.LoginResponse;
import com.github.zxs1994.java_template.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * AuthController 登录/认证接口
 * </p>
 *
 * @author xusheng
 * @since 2026-01-05
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "认证", description = "登录/认证接口")
public class AuthController {

    @Autowired
    private ISysUserService sysUserService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public LoginResponse login(@RequestBody LoginRequest req) {
        return sysUserService.login(req);
    }

    // 如果未来需要，可以加：
    // @PostMapping("/logout") ...
    // @PostMapping("/refresh-token") ...
}
