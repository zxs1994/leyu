package com.xusheng94.leyu.admin.model.query;

import com.xusheng94.leyu.common.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysOperationLogQuery extends BaseQuery {

    @Schema(description = "状态：1=成功，0=失败")
    private Boolean status;

    @Schema(description = "创建时间 - 开始")
    private OffsetDateTime createdAtStart;

    @Schema(description = "创建时间 - 结束")
    private OffsetDateTime createdAtEnd;

    @Schema(description = "用户ID")
    private Long userId;

}