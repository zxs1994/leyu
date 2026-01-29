package com.xusheng94.leyu.admin.config.swagger;

import com.xusheng94.leyu.admin.common.NoApiWrap;
import com.xusheng94.leyu.admin.config.AuthLevelResolver;
import com.xusheng94.leyu.admin.common.enums.AuthLevel;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import com.xusheng94.leyu.admin.common.ApiResponse;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SwaggerCustomizerProvider {

    private final AuthLevelResolver authLevelResolver;

    /**
     * 每个接口的 Operation 自定义，用于包装 200 响应为 ApiResponse<T>
     */
    OperationCustomizer apiResponseCustomizer() {
        return (operation, handlerMethod) -> {
            // 1️⃣ NoApiWrap
            boolean noWrap = handlerMethod.hasMethodAnnotation(NoApiWrap.class)
                    || handlerMethod.getBeanType().isAnnotationPresent(NoApiWrap.class);

            if (noWrap) return operation;

            // 2️⃣ 已经返回 ApiResponse<T>
            Class<?> returnType = handlerMethod.getMethod().getReturnType();
            if (ApiResponse.class.isAssignableFrom(returnType)) {
                return operation;
            }

            // 3️⃣ void / Void 也要包
            boolean isVoid =
                    returnType == void.class || returnType == Void.class;

            io.swagger.v3.oas.models.responses.ApiResponse response200 =
                    operation.getResponses().get("200");
            if (response200 == null) {
                return operation;
            }

            // 1️⃣ 如果没有 content（void / null）
            if (response200.getContent() == null || response200.getContent().isEmpty()) {

                MediaType mediaType = new MediaType();
                mediaType.setSchema(buildWrapperSchema(null));

                Content content = new Content();
                content.addMediaType("application/json", mediaType);

                response200.setContent(content);
                return operation;
            }

//            System.out.println((response200.getContent()) + " "  + returnType);

            // 2️⃣ 有 content（正常返回）
            response200.getContent().forEach((type, media) -> {
                Schema<?> originalSchema = media.getSchema();
                media.setSchema(buildWrapperSchema(originalSchema));
            });

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

    private Schema<?> buildWrapperSchema(Schema<?> dataSchema) {
        ObjectSchema wrapper = (ObjectSchema) new ObjectSchema()
                .addProperty("success", new BooleanSchema().example(true))
                .addProperty("code", new IntegerSchema().example(200));

        if (dataSchema != null) {
            wrapper.addProperty("data", dataSchema);
        } else {
            wrapper.addProperty("data", new Schema<>().nullable(true));
            // 👇 强制 example
            wrapper.setExample(buildVoidExample());
        }
        // 保持字段顺序的洁癖
        wrapper.addProperty("msg", new StringSchema().example("ok"))
               .addProperty("version", new StringSchema().example("1.0.0"));

        return wrapper;
    }
    private Map<String, Object> buildVoidExample() {
        Map<String, Object> example = new LinkedHashMap<>();
        example.put("success", true);
        example.put("code", 200);
        example.put("data", null);
        example.put("msg", "ok");
        example.put("version", "1.0.0");

        return example;
    }
}
