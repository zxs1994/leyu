package com.github.zxs1994.java_template.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import com.github.zxs1994.java_template.common.BaseEntity;

/**
 * <p>
 * 系统--角色表 实体
 * </p>
 *
 * @author xusheng
 * @since 2026-01-11 16:46:23
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys__role")
@Schema(description = "系统--角色表")
public class SysRole extends BaseEntity {

    @Schema(description = "角色名")
    private String name;

    @Schema(description = "角色编码")
    private String code;

    @Schema(description = "数据来源：SYSTEM=系统内置，USER=用户创建")
    private String source;

    @TableLogic
    @JsonIgnore
    @Schema(hidden = true)
    private Boolean deleted;

}