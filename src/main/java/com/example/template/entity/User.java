package com.example.template.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import com.example.template.common.BaseEntity;

/**
 * <p>
 * User 实体
 * </p>
 *
 * @author xusheng
 * @since 2026-01-02 19:11:11
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
@Schema(description = "")
public class User extends BaseEntity {

    @Schema(description = "")
    private Integer age;

    @Schema(description = "")
    private String email;

    @Schema(description = "用户名")
    private String name;

    @Schema(description = "")
    private Long jobId;

}