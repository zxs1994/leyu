package com.xusheng94.leyu.model.vo;

import com.xusheng94.leyu.entity.SysDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysDeptVo extends SysDept {

    @Schema(description = "负责人名称", example = "张三")
    private String leaderName;
}
