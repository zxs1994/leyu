package com.xusheng94.leyu.admin.common.enums;

import com.xusheng94.leyu.admin.common.BaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthLevel implements BaseEnum<Integer> {

    NORMAL(0, "权限校验", "blue"),
    WHITELIST(1, "白名单", "green"),
    LOGIN_ONLY(2, "登录即可", "cyan"),
    PLATFORM_ONLY(3, "仅平台用户", "red");

    private final Integer code;
    private final String desc;
    private final String color;

    public static AuthLevel fromCode(Integer code) {
        for (AuthLevel level : values()) {
            if (level.code.equals(code)) {
                return level;
            }
        }
//        return NORMAL;
        throw new IllegalArgumentException("未知 AuthLevel: " + code);
    }

    /** 是否需要登录 */
    public boolean needLogin() {
        return this != WHITELIST;
    }

    /** 是否需要权限校验 */
    public boolean needPermissionCheck() {
        return this == NORMAL || this == PLATFORM_ONLY;
    }

    /** 是否只对平台用户可见 */
    public boolean platformOnly() {
        return this == PLATFORM_ONLY;
    }

}
