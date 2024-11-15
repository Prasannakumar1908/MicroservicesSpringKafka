package com.prodify.apigateway.config;

//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.function.client.WebClient;
//
//@Configuration
//public class WebClientConfig {
//
//    @Bean
//    public WebClient webClient() {
//        return WebClient.builder().build();
//    }
//}
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced  // Enables load balancing for WebClient
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}

