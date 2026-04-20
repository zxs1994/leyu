package com.xusheng94.leyu.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import com.xusheng94.leyu.common.BaseEntity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * <p>
 * 数据权限测试表 实体
 * </p>
 *
 * @author xusheng
 * @since 2026-04-17 19:48:46
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_scope_test")
@Schema(description = "数据权限测试表")
public class DataScopeTest extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键", example = "8088")
    private Long id;

    @JsonIgnore
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "租户 / 公司ID（SaaS隔离）", example = "8088")
    private Long tenantId;

    @Schema(description = "名称")
    private String name;

    @TableLogic
    @JsonIgnore
    @Schema(description = "逻辑删除")
    private Boolean deleted;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "部门ID", example = "8088")
    private Long deptId;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @TableField(fill = FieldFill.INSERT)
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "创建者ID", example = "8088")
    private Long creatorId;

}