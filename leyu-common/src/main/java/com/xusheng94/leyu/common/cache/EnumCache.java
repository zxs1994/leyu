package com.xusheng94.leyu.common.cache;

import com.xusheng94.leyu.common.util.EnumUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EnumCache {

    private Map<String, List<Map<String, Object>>> enumCache;

    @PostConstruct
    public void init() {
        this.enumCache = EnumUtils.loadAllEnums();
    }

    public Map<String, List<Map<String, Object>>> getAll() {
        return enumCache;
    }
}
