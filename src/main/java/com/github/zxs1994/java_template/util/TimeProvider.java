package com.example.template.util;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class TimeProvider {

    private static final ZoneOffset CN_OFFSET =
        ZoneOffset.ofHours(8);

    private TimeProvider() {}

    /**
     * 确保北京时间
     */
    public static OffsetDateTime now() {
        return OffsetDateTime.now(CN_OFFSET);
    }
}
