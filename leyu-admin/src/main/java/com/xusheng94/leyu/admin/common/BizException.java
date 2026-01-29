package com.xusheng94.leyu.admin.common;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException {
    private final int code; // 自定义业务码

    public BizException(int code, String msg) {
        super(msg); // 异常信息
        this.code = code;
    }

}
