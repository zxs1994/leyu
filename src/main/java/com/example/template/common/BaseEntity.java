package com.example.template.common;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class BaseEntity {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键", accessMode = Schema.AccessMode.READ_WRITE)
    private Long id;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private OffsetDateTime createdAt;

    @Schema(description = "更新时间", accessMode = Schema.AccessMode.READ_ONLY)
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private OffsetDateTime updatedAt;

    @Schema(description = "是否删除", accessMode = Schema.AccessMode.READ_ONLY)
    @TableLogic
    @TableField(value = "deleted")
    private Boolean deleted;
}
