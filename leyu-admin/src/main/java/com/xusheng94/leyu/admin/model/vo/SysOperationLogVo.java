package com.xusheng94.leyu.admin.model.vo;

import com.xusheng94.leyu.admin.entity.SysOperationLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysOperationLogVo extends SysOperationLog {

    // TODO VO 扩展字段写在这里
    @Schema(description = "用户名")
    private String username;
}