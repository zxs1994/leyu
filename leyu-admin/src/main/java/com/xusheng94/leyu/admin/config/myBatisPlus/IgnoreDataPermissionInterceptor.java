package com.xusheng94.leyu.admin.config.myBatisPlus;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class IgnoreDataPermissionInterceptor implements HandlerInterceptor {

    public static final String IGNORE_DATA_PERMISSION_ATTR =
            IgnoreDataPermissionInterceptor.class.getName() + ".ignore";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        boolean ignoreDataPermission =
                AnnotatedElementUtils.hasAnnotation(handlerMethod.getMethod(), IgnoreDataPermission.class)
                        || AnnotatedElementUtils.hasAnnotation(handlerMethod.getBeanType(), IgnoreDataPermission.class);

        if (ignoreDataPermission) {
            request.setAttribute(IGNORE_DATA_PERMISSION_ATTR, true);
        }
        return true;
    }
}