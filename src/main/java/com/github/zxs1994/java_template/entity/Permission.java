package com.example.template.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import com.example.template.common.BaseEntity;

/**
 * <p>
 * 权限表 实体
 * </p>
 *
 * @author xusheng
 * @since 2026-01-06 09:59:56
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("permission")
@Schema(description = "权限表")
public class Permission extends BaseEntity {

    @Schema(description = "权限名")
    private String name;

    @Schema(description = "权限编码")
    private String code;

}