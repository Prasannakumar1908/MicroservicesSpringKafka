//package com.prodify.apigateway.config;
//
//import org.springframework.cloud.gateway.config.GlobalCorsProperties;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.cloud.gateway.filter.GatewayFilterSpec;
//
//@Configuration
//public class ApiGatewayConfig {
//
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                .route(r -> r.path("/gateway/orders/**")
//                        .uri("lb://order-service")
//                        .id("order-service")
//                        .filters(f -> f.rewritePath("/gateway/orders/(?<segment>.*)", "/${segment}")) // Rewrite path
//                )
//                .route(r -> r.path("/gateway/products/**")
//                        .uri("lb://product-service")
//                        .id("product-service")
//                        .filters(f -> f.rewritePath("/gateway/products/(?<segment>.*)", "/${segment}")) // Rewrite path
//                )
//                .route(r -> r.path("/gateway/users/**")
//                        .uri("lb://user-service")
//                        .id("user-service")
//                        .filters(f -> f.rewritePath("/gateway/users/(?<segment>.*)", "/${segment}")) // Rewrite path
//                )
//                .build();
//    }
//}
