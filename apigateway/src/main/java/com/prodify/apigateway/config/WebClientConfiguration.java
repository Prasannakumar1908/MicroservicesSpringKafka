package com.prodify.apigateway.config;

import com.prodify.apigateway.util.RequestIdContext;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfiguration {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .filter(this::addRequestIdFilter);
    }

    private Mono<ClientResponse> addRequestIdFilter(ClientRequest request, ExchangeFunction next) {
        return Mono.deferContextual(ctx -> {
            // Retrieve RequestId from Reactor Context
            String requestId = ctx.getOrDefault("requestId", "Unknown-RequestId");

            // Add the RequestId to the outgoing request headers
            ClientRequest newRequest = ClientRequest.from(request)
                    .header("X-Request-Id", requestId)
                    .build();

            return next.exchange(newRequest);
        });
    }
}
