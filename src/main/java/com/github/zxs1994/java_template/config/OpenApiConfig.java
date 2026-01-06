package com.example.template.config;

import com.example.template.common.NoApiWrap;
import com.example.template.util.LoadProperties;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

@Configuration
public class OpenApiConfig {

    /**
     * 每个接口的 Operation 自定义，用于包装 200 响应为 ApiResponse<T>
     */
    @Bean
    public OperationCustomizer apiResponseOperationCustomizer() {
        return (operation, handlerMethod) -> {

            // 判断方法或类是否标注 @NoApiWrap
            boolean noWrap = handlerMethod.hasMethodAnnotation(NoApiWrap.class)
                    || handlerMethod.getBeanType().isAnnotationPresent(NoApiWrap.class);

            // 确保 extensions 不为 null
            if (operation.getExtensions() == null) {
                operation.setExtensions(new LinkedHashMap<>());
            }
            // 保存标记
            operation.getExtensions().put("noApiWrap", noWrap);

            // 1️⃣ @NoApiWrap → 不包装
            if (noWrap) {
                return operation;
            }

            // 2️⃣ 返回值本身是 ApiResponse → 不包装
            Class<?> returnType = handlerMethod.getMethod().getReturnType();
            if (ApiResponse.class.isAssignableFrom(returnType)) {
                return operation;
            }

            // 3️⃣ 否则才包装 200
            ApiResponse response200 = operation.getResponses().get("200");
            if (response200 != null && response200.getContent() != null) {
                response200.getContent().forEach((mediaType, media) -> {
                    Schema<?> originalSchema = media.getSchema();
                    if (originalSchema == null) return;

                    // 跳过 void / binary
                    if ("string".equals(originalSchema.getType()) &&
                            "binary".equals(originalSchema.getFormat())) return;

                    // 包装成 ApiResponse<T>
                    Schema<?> wrapper = new ObjectSchema()
                            .addProperty("code", new IntegerSchema().example(200))
                            .addProperty("msg", new StringSchema().example("ok"))
                            .addProperty("data", originalSchema);

                    media.setSchema(wrapper);
                });
            }

            return operation;
        };
    }

    /**
     * 用于控制每个接口右边有没有🔒, 不在白名单的都加锁
     */
    @Bean
    public OperationCustomizer securityOperationCustomizer(@Value("${security.permit-urls}") String permitUrls) {
        // 白名单 URL 列表
        List<String> urls = Arrays.stream(permitUrls.split(","))
                .map(String::trim)
                .toList();

        return (operation, handlerMethod) -> {
            // 取方法路径
            String methodPath = null;
            if (handlerMethod.hasMethodAnnotation(GetMapping.class)) {
                GetMapping m = handlerMethod.getMethodAnnotation(GetMapping.class);
                if (m.value().length > 0) methodPath = m.value()[0];
            } else if (handlerMethod.hasMethodAnnotation(PostMapping.class)) {
                PostMapping m = handlerMethod.getMethodAnnotation(PostMapping.class);
                if (m.value().length > 0) methodPath = m.value()[0];
            } else if (handlerMethod.hasMethodAnnotation(PutMapping.class)) {
                PutMapping m = handlerMethod.getMethodAnnotation(PutMapping.class);
                if (m.value().length > 0) methodPath = m.value()[0];
            } else if (handlerMethod.hasMethodAnnotation(DeleteMapping.class)) {
                DeleteMapping m = handlerMethod.getMethodAnnotation(DeleteMapping.class);
                if (m.value().length > 0) methodPath = m.value()[0];
            } else if (handlerMethod.hasMethodAnnotation(PatchMapping.class)) {
                PatchMapping m = handlerMethod.getMethodAnnotation(PatchMapping.class);
                if (m.value().length > 0) methodPath = m.value()[0];
            } else if (handlerMethod.hasMethodAnnotation(RequestMapping.class)) {
                RequestMapping m = handlerMethod.getMethodAnnotation(RequestMapping.class);
                if (m.value().length > 0) methodPath = m.value()[0];
            }

            // 类级别 @RequestMapping
            String classPath = "";
            if (handlerMethod.getBeanType().isAnnotationPresent(RequestMapping.class)) {
                RequestMapping cm = handlerMethod.getBeanType().getAnnotation(RequestMapping.class);
                if (cm.value().length > 0) classPath = cm.value()[0];
            }

            // 拼接完整路径并规范斜杠
            String fullPath = (classPath.endsWith("/") ? classPath.substring(0, classPath.length() - 1) : classPath)
                    + (methodPath != null ? (methodPath.startsWith("/") ? methodPath : "/" + methodPath) : "");

            // 判断是否在白名单
            boolean isPermit = urls.stream().anyMatch(urlPattern -> {
                if (urlPattern.endsWith("/**")) {
                    String prefix = urlPattern.substring(0, urlPattern.length() - 3);
                    return fullPath.startsWith(prefix);
                } else if (urlPattern.endsWith("/*")) {
                    String prefix = urlPattern.substring(0, urlPattern.length() - 2);
                    if (!fullPath.startsWith(prefix)) return false;
                    String suffix = fullPath.substring(prefix.length());
                    return !suffix.substring(1).contains("/");
                } else {
                    return urlPattern.equals(fullPath);
                }
            });

            // 不是白名单接口才加 JWT
            if (!isPermit) {
                operation.addSecurityItem(new SecurityRequirement().addList("jwt"));
            }

            return operation;
        };
    }

    /**
     * OpenAPI 全局配置 JWT 安全
     */
    @Bean
    public OpenAPI openAPI() {

        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");
        Properties ProjectProps = LoadProperties.loadProject();
        return new OpenAPI()
                .info(new Info()
                        .title(ProjectProps.getProperty("project.name"))
                        .version(ProjectProps.getProperty("project.version"))
                        .description(ProjectProps.getProperty("project.description"))
                )
                .components(new Components().addSecuritySchemes("jwt", securityScheme));
    }

}