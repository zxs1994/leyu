package com.github.zxs1994.java_template.config.swagger;

import com.github.zxs1994.java_template.common.NoApiWrap;
import com.github.zxs1994.java_template.config.AuthLevelResolver;
import com.github.zxs1994.java_template.enums.AuthLevel;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SwaggerCustomizerProvider {

    private final AuthLevelResolver authLevelResolver;

    /**
     * 每个接口的 Operation 自定义，用于包装 200 响应为 ApiResponse<T>
     */
    OperationCustomizer apiResponseCustomizer() {
        return (operation, handlerMethod) -> {
            boolean noWrap = handlerMethod.hasMethodAnnotation(NoApiWrap.class)
                    || handlerMethod.getBeanType().isAnnotationPresent(NoApiWrap.class);

            if (noWrap) return operation;

            Class<?> returnType = handlerMethod.getMethod().getReturnType();
            if (io.swagger.v3.oas.models.responses.ApiResponse.class
                    .isAssignableFrom(returnType)) {
                return operation;
            }

            io.swagger.v3.oas.models.responses.ApiResponse response200 =
                    operation.getResponses().get("200");

            if (response200 != null && response200.getContent() != null) {
                response200.getContent().forEach((mediaType, media) -> {
                    Schema<?> originalSchema = media.getSchema();
                    if (originalSchema == null) return;

                    Schema<?> wrapper = new ObjectSchema()
                            .addProperty("success", new BooleanSchema().example(true))
                            .addProperty("code", new IntegerSchema().example(200))
                            .addProperty("data", originalSchema)
                            .addProperty("msg", new StringSchema().example("ok"))
                            .addProperty("version", new StringSchema().example("1.0.0"));

                    media.setSchema(wrapper);
                });
            }
            return operation;
        };
    }

    /**
     * 用于控制每个接口右边有没有🔒, 不在白名单的都加锁
     */
    OpenApiCustomizer securityCustomizer() {

        return openApi -> openApi.getPaths().forEach((path, pathItem) -> {
            AuthLevel level = authLevelResolver.resolve(path);
            if (level != AuthLevel.WHITELIST) {
                pathItem.readOperations().forEach(op ->
                        op.addSecurityItem(new SecurityRequirement().addList("jwt"))
                );
            }
        });
    }
}
