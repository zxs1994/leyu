package com.xusheng94.leyu.admin.model.query;

import com.xusheng94.leyu.admin.common.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;

public class SysUserQuery extends BaseQuery {

    @Schema(description="用户名称")
    private String name;

}
