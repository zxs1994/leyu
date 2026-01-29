package com.xusheng94.leyu.admin.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysPermissionDto {

    @Schema(description = "排序")
    private Integer sort;
}
