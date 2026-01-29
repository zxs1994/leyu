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
 * 系统--组织部门表 实体
 * </p>
 *
 * @author xusheng
 * @since 2026-01-25 16:14:29
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys__dept")
@Schema(description = "系统--组织部门表")
public class SysDept extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键", example = "8088")
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "父部门ID", example = "8088")
    private Long parentId;

    @JsonIgnore
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "租户 / 公司ID（SaaS隔离）", example = "8088")
    private Long tenantId;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "部门编码")
    private String code;

    @Schema(description = "层级路径，如 /1/3/8")
    private String path;

    @Schema(description = "排序")
    private Integer sort;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "负责人用户ID", example = "8088")
    private Long leaderId;

    @Schema(description = "状态：1=启用，0=停用")
    private Boolean status;

    @TableLogic
    @JsonIgnore
    @Schema(description = "逻辑删除")
    private Boolean deleted;

}