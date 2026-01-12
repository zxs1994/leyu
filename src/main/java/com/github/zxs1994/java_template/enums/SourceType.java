package com.github.zxs1994.java_template.enums;

import com.github.zxs1994.java_template.common.BaseEnum;
import lombok.Getter;

@Getter
public enum SourceType implements BaseEnum<String> {

    SYSTEM("SYSTEM", "系统内置"),
    USER("USER", "用户创建");

    private final String code;
    private final String desc;

    SourceType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SourceType fromCode(String code) {
        for (SourceType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知数据来源类型: " + code);
    }
}