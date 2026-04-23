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
 * 系统--操作日志表 实体
 * </p>
 *
 * @author xusheng
 * @since 2026-04-23 17:05:58
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys__operation_log")
@Schema(description = "系统--操作日志表")
public class SysOperationLog extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键", example = "8088")
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "用户ID", example = "8088")
    private Long userId;

    @JsonIgnore
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "租户 / 公司ID（SaaS隔离）", example = "8088")
    private Long tenantId;

    @Schema(description = "操作行为")
    private String action;

    @Schema(description = "业务模块")
    private String module;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "业务数据ID", example = "8088")
    private String dataId;

    @Schema(description = "请求方式")
    private String method;

    @Schema(description = "请求路径")
    private String path;

    @Schema(description = "状态：1=成功，0=失败")
    private Boolean status;

    @Schema(description = "失败原因")
    private String errorMsg;

    @Schema(description = "IP地址")
    private String ip;

    @Schema(description = "用户代理")
    private String userAgent;

    @TableLogic
    @JsonIgnore
    @Schema(description = "逻辑删除")
    private Boolean deleted;

}