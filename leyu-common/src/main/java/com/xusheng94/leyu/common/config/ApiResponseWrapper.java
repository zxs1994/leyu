package com.xusheng94.leyu.common.config;

import com.xusheng94.leyu.common.BizException;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import com.xusheng94.leyu.common.ApiResponse;
import com.xusheng94.leyu.common.NoApiWrap;

import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiResponseWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType) {

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
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType contentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response) {

        String path = request.getURI().getPath();
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            return body;
        }

        if (body instanceof ApiResponse) {
            return body;
        }

        // 下载/二进制响应：避免被 ApiResponse 包装导致类型转换错误
        if (body instanceof byte[] || body instanceof Resource) {
            return body;
        }

        // 兜底：按响应头判断为二进制时直接放行
        if (contentType != null && MediaType.APPLICATION_OCTET_STREAM.includes(contentType)) {
            return body;
        }

        // 普通对象直接包装
        return ApiResponse.success(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<String> handleValidationException(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(f -> f.getField() + ":" + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ApiResponse.fail(400, msg);
    }

    @ExceptionHandler(BizException.class)
    public ApiResponse<String> handleBizException(BizException ex) {
        return ApiResponse.fail(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ApiResponse<String> handleNotFoundException(NotFoundException ex) {
        return ApiResponse.fail(404, ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ApiResponse<String> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ApiResponse.fail(404, ex.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ApiResponse<String> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return ApiResponse.fail(404, ex.getMessage());
    }

    // 通用异常处理（兜底）
    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception ex) {
        return ApiResponse.fail(500, ex.getMessage());
    }
}
