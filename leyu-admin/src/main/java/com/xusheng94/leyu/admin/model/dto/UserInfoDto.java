package com.xusheng94.leyu.admin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserInfoDto {

    @Schema(description = "用户名")
    private String name;

}
