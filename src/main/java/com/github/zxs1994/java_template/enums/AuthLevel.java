package com.github.zxs1994.java_template.enums;

import com.github.zxs1994.java_template.common.BaseEnum;
import lombok.Getter;

@Getter
public enum AuthLevel implements BaseEnum<Integer> {

    NORMAL(0, "需要权限校验"),
    WHITELIST(1, "白名单"),
    LOGIN_ONLY(2, "登录即可");

    private final Integer code;
    private final String desc;

    AuthLevel(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AuthLevel fromCode(Integer code) {
        for (AuthLevel level : values()) {
            if (level.code.equals(code)) {
                return level;
            }
        }
//        return NORMAL;
        throw new IllegalArgumentException("未知 AuthLevel: " + code);
    }
}
