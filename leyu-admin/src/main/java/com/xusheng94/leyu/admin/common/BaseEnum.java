package com.xusheng94.leyu.admin.common;

public interface BaseEnum<T> {
    T getCode();
    String getDesc();

    // 默认实现，旧枚举不受影响
    default String getColor() {
        return null;
    }
}
