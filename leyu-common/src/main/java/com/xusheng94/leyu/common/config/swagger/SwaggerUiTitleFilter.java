package com.xusheng94.leyu.common.config.swagger;

import com.xusheng94.leyu.common.config.ProjectProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@RequiredArgsConstructor
public class SwaggerUiTitleFilter extends OncePerRequestFilter {

    private final ProjectProperties projectProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri == null || !uri.startsWith("/swagger-ui/") || !uri.endsWith("index.html");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(request, wrapper);

        String contentType = wrapper.getContentType();
        if (StringUtils.hasText(contentType) && contentType.contains("text/html")) {
            Charset charset = Charset.forName(wrapper.getCharacterEncoding());
            String body = new String(wrapper.getContentAsByteArray(), charset);
                String title = StringUtils.hasText(projectProperties.getModuleName())
                    ? projectProperties.getModuleName()
                    : projectProperties.getName();
            String updated = body.replaceFirst("<title>.*?</title>", "<title>" + title + "</title>");

            response.resetBuffer();
            response.setCharacterEncoding(wrapper.getCharacterEncoding());
            response.setContentType(wrapper.getContentType());
            response.setContentLength(updated.getBytes(charset).length);
            response.getOutputStream().write(updated.getBytes(charset));
        } else {
            wrapper.copyBodyToResponse();
        }
    }
}
