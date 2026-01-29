package com.xusheng94.leyu.admin.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "分页基础参数")
public class BaseQuery {

    @Schema(description = "页码（从 1 开始）", example = "1")
    private long page = 1;

    @Schema(description = "每页大小", example = "10")
    private long size = 10;

    @Schema(description = "名称")
    private String name;

}
