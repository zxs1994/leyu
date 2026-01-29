package com.xusheng94.leyu.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import com.xusheng94.leyu.admin.common.BaseEntity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * <p>
 * 系统--角色-权限关联表 实体
 * </p>
 *
 * @author xusheng
 * @since 2026-01-22 19:50:06
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys__role_permission")
@Schema(description = "系统--角色-权限关联表")
public class SysRolePermission extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键", example = "8088")
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "角色ID", example = "8088")
    private Long roleId;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "权限ID", example = "8088")
    private Long permissionId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "数据来源：SYSTEM=系统内置，USER=用户创建")
    private String source;

}