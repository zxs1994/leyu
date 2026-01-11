package com.github.zxs1994.java_template.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import com.github.zxs1994.java_template.common.BaseEntity;

/**
 * <p>
 * 系统--用户-角色关联表 实体
 * </p>
 *
 * @author xusheng
 * @since 2026-01-11 16:46:23
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys__user_role")
@Schema(description = "系统--用户-角色关联表")
public class SysUserRole extends BaseEntity {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "数据来源：SYSTEM=系统内置，USER=用户创建")
    private String source;

}