package com.prodify.apigateway;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Routes for various microservices
                .route("auth-service", r -> r.path("/auth-service/**")
                        .uri("lb://auth-service"))  // Dynamic discovery with Eureka

                .route("common-service", r -> r.path("/common-service/**")
                        .uri("lb://common-service"))

                .route("order-service", r -> r.path("/orders/**")
                        .uri("lb://order-service"))

                .route("payment-service", r -> r.path("/payment-service/**")
                        .uri("lb://payment-service"))

                .route("product-service", r -> r.path("/product-service/**")
                        .uri("lb://product-service"))

                .route("shipment-service", r -> r.path("/shipment-service/**")
                        .uri("lb://shipment-service"))

                .route("user-service", r -> r.path("/user-service/**")
                        .uri("lb://user-service"))

                .build();
    }

    // Example of adding a global filter (optional)
    @Bean
    public GlobalFilter customFilter() {
        return (ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) -> {
            // Add custom logic here
            return chain.filter(exchange);
        };
    }
}
