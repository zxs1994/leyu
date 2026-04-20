package com.xusheng94.leyu.common.enums;

import com.xusheng94.leyu.common.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DataScopeType implements BaseEnum<String> {

    ALL("ALL", "全部数据"),
    DEPT_AND_CHILD("DEPT_AND_CHILD", "本部门及以下数据"),
    DEPT("DEPT", "本部门数据"),
    SELF("SELF", "仅本人数据");

    private final String code;
    private final String desc;

}
