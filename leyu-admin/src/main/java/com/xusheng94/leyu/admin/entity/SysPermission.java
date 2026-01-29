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
 * 系统--权限表 实体
 * </p>
 *
 * @author xusheng
 * @since 2026-01-22 19:50:06
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys__permission")
@Schema(description = "系统--权限表")
public class SysPermission extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键", example = "8088")
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "父权限ID", example = "8088")
    private Long parentId;

    @Schema(description = "权限名")
    private String name;

    @Schema(description = "权限编码")
    private String code;

    @Schema(description = "请求方式")
    private String method;

    @Schema(description = "接口路径")
    private String path;

    @Schema(description = "权限模块名称")
    private String moduleName;

    @Schema(description = "访问级别：0权限校验 1白名单 2登录即可")
    private Integer authLevel;

    @Schema(description = "状态：1=启用，0=停用")
    private Boolean status;

}