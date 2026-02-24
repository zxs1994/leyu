package com.xusheng94.leyu.admin.config.swagger;

import com.xusheng94.leyu.common.config.ProjectProperties;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.xusheng94.leyu.common.config.swagger.SwaggerCustomizerProvider;

import java.util.List;


@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    private final SwaggerCustomizerProvider provider;

    private final ProjectProperties projectProperties;

    /**
     * OpenAPI 全局配置 JWT 安全
     */
    @Bean
    public OpenAPI openAPI(@Value("${server.port}") String port,
                           @Value("${spring.profiles.active}") String profile) {

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        String url;

        if ("dev".equals(profile)) {
            url = "http://localhost:" + port;
        } else  {
            url = "/api";
        }

        return new OpenAPI()
                .info(new Info()
                        .title(projectProperties.getName())
                        .version(projectProperties.getVersion())
                        .description(projectProperties.getDescription())
                )
                .servers(List.of(
                        new Server().url(url).description(profile + "环境")
                ))
                .components(new Components().addSecuritySchemes("jwt", securityScheme));
    }
    @Bean
    @ConditionalOnProperty(
            name = "springdoc.swagger-ui.group-enabled",
            havingValue = "false",
            //  👉 把“没配置”当成 false
            matchIfMissing = true
    )
    public OperationCustomizer defaultOperationCustomizer() {
        return provider.apiResponseCustomizer();
    }

    @Bean
    @ConditionalOnProperty(
            name = "springdoc.swagger-ui.group-enabled",
            havingValue = "false",
            //  👉 把“没配置”当成 false
            matchIfMissing = true
    )
    public OpenApiCustomizer defaultOpenApiCustomizer() {
        return provider.securityCustomizer();
    }

    @Bean
    @ConditionalOnProperty(
            name = "springdoc.swagger-ui.group-enabled",
            havingValue = "true"
    )
    public GroupedOpenApi platformApi() {
        return baseBuilder("平台管理")
                .pathsToMatch("/platform/**")
                .build();
    }

    @Bean
    @ConditionalOnProperty(
            name = "springdoc.swagger-ui.group-enabled",
            havingValue = "true"
    )
    public GroupedOpenApi sysApi() {
        return baseBuilder("系统管理")
                .pathsToMatch("/sys/**")
                .build();
    }

    @Bean
    @ConditionalOnProperty(
            name = "springdoc.swagger-ui.group-enabled",
            havingValue = "true"
    )
    public GroupedOpenApi bizApi() {
        return baseBuilder("业务接口")
                .pathsToExclude("/sys/**", "/platform/**")
                .build();
    }

    private GroupedOpenApi.Builder baseBuilder(String group) {
        return GroupedOpenApi.builder()
                .group(group)
                .addOperationCustomizer(provider.apiResponseCustomizer())
                .addOpenApiCustomizer(provider.securityCustomizer());
    }


}