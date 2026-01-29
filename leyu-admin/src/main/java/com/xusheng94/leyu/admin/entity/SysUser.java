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
 * 系统--用户表 实体
 * </p>
 *
 * @author xusheng
 * @since 2026-01-22 19:50:06
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys__user")
@Schema(description = "系统--用户表")
public class SysUser extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键", example = "8088")
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "所属部门ID", example = "8088")
    private Long deptId;

    @JsonIgnore
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "租户 / 公司ID（SaaS隔离）", example = "8088")
    private Long tenantId;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "用户名")
    private String name;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "密码")
    private String password;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "token版本")
    private Integer tokenVersion;

    @Schema(description = "排序")
    private Integer sort;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "数据来源：SYSTEM=系统内置，USER=用户创建")
    private String source;

    @Schema(description = "状态：1=启用，0=停用")
    private Boolean status;

    @TableLogic
    @JsonIgnore
    @Schema(description = "逻辑删除")
    private Boolean deleted;

}