package com.github.zxs1994.java_template.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "project")
public class ProjectProperties {
    private String name;
    private String description;
    private String basePackage;
    private String version;
}
