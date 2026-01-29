package com.xusheng94.leyu.controller;

import com.xusheng94.leyu.model.dto.LoginDto;
import com.xusheng94.leyu.model.vo.LoginVo;
import com.xusheng94.leyu.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * AuthController 登录/认证接口
 * </p>
 *
 * @author xusheng
 * @since 2026-01-05
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "认证", description = "登录/认证接口")
public class AuthController {

    private final ISysUserService sysUserService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public LoginVo login(@RequestBody LoginDto req) {
        return sysUserService.login(req);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public void logout() {
        sysUserService.logout();
    }

}
