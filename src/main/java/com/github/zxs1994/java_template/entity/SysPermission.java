package com.github.zxs1994.java_template.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import com.github.zxs1994.java_template.common.BaseEntity;

/**
 * <p>
 * 系统--权限表 实体
 * </p>
 *
 * @author xusheng
 * @since 2026-01-11 16:46:23
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys__permission")
@Schema(description = "系统--权限表")
public class SysPermission extends BaseEntity {

    @Schema(description = "权限名")
    private String name;

    @Schema(description = "权限编码")
    private String code;

    @Schema(description = "请求方式")
    private String method;

    @Schema(description = "接口路径")
    private String path;

    @Schema(description = "权限模块")
    private String module;

    @Schema(description = "权限模块名称")
    private String moduleName;

    @Schema(description = "访问级别：0权限校验 1白名单")
    private Integer authLevel;

    @Schema(description = "父权限ID")
    private Long parentId;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "逻辑删除")
    private Boolean del;

}