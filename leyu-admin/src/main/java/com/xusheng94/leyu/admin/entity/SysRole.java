package com.xusheng94.leyu.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import com.xusheng94.leyu.admin.common.BaseEntity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * <p>
 * 系统--角色表 实体
 * </p>
 *
 * @author xusheng
 * @since 2026-01-22 19:50:06
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys__role")
@Schema(description = "系统--角色表")
public class SysRole extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键", example = "8088")
    private Long id;

    @JsonIgnore
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "租户 / 公司ID（SaaS隔离）", example = "8088")
    private Long tenantId;

    @Schema(description = "角色名")
    private String name;

    @Schema(description = "角色编码")
    private String code;

    @TableLogic
    @JsonIgnore
    @Schema(description = "逻辑删除")
    private Boolean deleted;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "数据来源：SYSTEM=系统内置，USER=用户创建")
    private String source;

}