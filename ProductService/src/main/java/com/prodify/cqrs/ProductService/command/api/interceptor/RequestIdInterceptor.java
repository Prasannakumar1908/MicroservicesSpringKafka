package com.prodify.cqrs.ProductService.command.api.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class RequestIdInterceptor implements HandlerInterceptor {

    public static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            log.warn("No RequestId found in headers");
        } else {
            log.info("RequestId: {} received", requestId);
        }

        return true; // Continue the request
    }
}