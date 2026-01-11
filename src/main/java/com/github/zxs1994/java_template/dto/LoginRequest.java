package com.github.zxs1994.java_template.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequest {
    @Schema(description = "邮箱", example = "admin@qq.com")
    private String email;
    @Schema(description = "密码", example = "admin123")
    private String password;
}
