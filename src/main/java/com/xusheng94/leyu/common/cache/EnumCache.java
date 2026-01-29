package com.xusheng94.leyu.common.cache;

import com.xusheng94.leyu.config.ProjectProperties;
import com.xusheng94.leyu.common.util.EnumUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnumCache {

    private Map<String, List<Map<String, Object>>> enumCache;

    private final ProjectProperties projectProperties;

    @PostConstruct
    public void init() {
        this.enumCache = EnumUtils.loadAllEnums(
                projectProperties.getBasePackage() + ".common.enums"
        );
        log.info("enum cache init success");
    }

    public Map<String, List<Map<String, Object>>> getAll() {
        return enumCache;
    }
}
