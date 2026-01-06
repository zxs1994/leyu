package com.example.template.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import com.example.template.common.BaseEntity;

/**
 * <p>
 * 用户-角色关联表 实体
 * </p>
 *
 * @author xusheng
 * @since 2026-01-06 09:59:56
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_role")
@Schema(description = "用户-角色关联表")
public class UserRole extends BaseEntity {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "角色ID")
    private Long roleId;

}