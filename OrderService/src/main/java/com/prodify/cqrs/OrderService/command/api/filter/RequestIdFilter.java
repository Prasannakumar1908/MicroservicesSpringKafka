package com.prodify.cqrs.OrderService.command.api.filter;

import com.prodify.cqrs.OrderService.command.api.util.RequestIdContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class RequestIdFilter extends OncePerRequestFilter {

    private final RequestIdContext requestIdContext;

    public RequestIdFilter(RequestIdContext requestIdContext) {
        this.requestIdContext = requestIdContext;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, jakarta.servlet.ServletException {
        try {
            String requestId = request.getHeader("X-Request-Id");
            if (requestId != null && !requestId.isEmpty()) {
                requestIdContext.setRequestId(requestId);
                log.info("RequestId [{}] extracted from headers", requestId);
            } else {
                log.warn("No RequestId found in headers");
            }
            filterChain.doFilter(request, response);
        } finally {
            requestIdContext.clear(); // Ensure the context is cleared after the request.
        }
    }
}
