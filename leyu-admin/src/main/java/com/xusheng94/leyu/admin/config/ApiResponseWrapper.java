package com.xusheng94.leyu.admin.config;

import com.xusheng94.leyu.admin.common.BizException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import com.xusheng94.leyu.admin.common.ApiResponse;
import com.xusheng94.leyu.admin.common.NoApiWrap;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiResponseWrapper implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {

        // 方法上标注
        if (returnType.hasMethodAnnotation(NoApiWrap.class)) {
            return false;
        }

        // 类上标注
        Class<?> declaringClass = returnType.getDeclaringClass();
        if (declaringClass.isAnnotationPresent(NoApiWrap.class)) {
            return false;
        }

        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType contentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        String path = request.getURI().getPath();
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            return body;
        }

        if (body instanceof ApiResponse) {
            return body;
        }

        // 普通对象直接包装
        return ApiResponse.success(body);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception ex) {
        return ApiResponse.fail(500, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<String> handleValidationException(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(f -> f.getField() + ":" + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ApiResponse.fail(400, msg);
    }

    @ExceptionHandler(NotFoundException.class)
    public ApiResponse<String> handleNotFoundException(NotFoundException ex) {
        return ApiResponse.fail(404, ex.getMessage());
    }

    @ExceptionHandler(BizException.class)
    public ApiResponse<String> handleBizException(BizException ex) {
        return ApiResponse.fail(ex.getCode(), ex.getMessage());
    }
}
