package com.xusheng94.leyu.common.enums;

import com.xusheng94.leyu.common.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SourceType implements BaseEnum<String> {

    SYSTEM("SYSTEM", "系统内置"),
    USER("USER", "用户创建");

    private final String code;
    private final String desc;

    public static SourceType fromCode(String code) {
        for (SourceType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知数据来源类型: " + code);
    }
}