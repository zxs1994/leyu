package com.example.template.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import com.example.template.common.BaseEntity;

/**
 * <p>
 * 角色表 实体
 * </p>
 *
 * @author xusheng
 * @since 2026-01-06 09:59:56
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("role")
@Schema(description = "角色表")
public class Role extends BaseEntity {

    @Schema(description = "角色名")
    private String name;

    @Schema(description = "角色编码")
    private String code;

}