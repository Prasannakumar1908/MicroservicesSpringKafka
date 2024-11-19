package com.prodify.apigateway.filter;

import com.prodify.apigateway.util.RequestIdContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
public class RequestIdFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // Extract or generate the RequestId
        String requestId = exchange.getRequest().getHeaders().getFirst("X-Request-Id");
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
            log.info("Generated new RequestId: {}", requestId);
        }

        String finalRequestId = requestId;

        // Add the RequestId to the response headers and Reactor Context
        exchange.getResponse().getHeaders().add("X-Request-Id", finalRequestId);

        return chain.filter(exchange)
                .contextWrite(context -> context.put("requestId", finalRequestId));
    }
}
