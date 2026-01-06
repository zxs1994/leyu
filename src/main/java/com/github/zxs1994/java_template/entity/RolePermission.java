package com.example.template.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import com.example.template.common.BaseEntity;

/**
 * <p>
 * 角色-权限关联表 实体
 * </p>
 *
 * @author xusheng
 * @since 2026-01-06 09:59:56
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("role_permission")
@Schema(description = "角色-权限关联表")
public class RolePermission extends BaseEntity {

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "权限ID")
    private Long permissionId;

}