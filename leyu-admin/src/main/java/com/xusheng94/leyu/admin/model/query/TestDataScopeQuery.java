package com.xusheng94.leyu.admin.model.query;

import com.xusheng94.leyu.common.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class TestDataScopeQuery extends BaseQuery {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "创建时间 - 开始")
    private OffsetDateTime createdAtStart;

    @Schema(description = "创建时间 - 结束")
    private OffsetDateTime createdAtEnd;

}