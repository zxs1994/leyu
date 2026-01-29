package com.xusheng94.leyu.common.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public final class QwUtils {

    private QwUtils() {}

    public static <T> QueryWrapper<T> listQw() {
        QueryWrapper<T> qw = new QueryWrapper<>();

        // 默认排序
        // 数值越小越靠前 拖拽排序 上移 / 下移 权重调整 都会更自然。
        qw.orderByAsc("sort")
                .orderByDesc("created_at");

        return qw;
    }
}
