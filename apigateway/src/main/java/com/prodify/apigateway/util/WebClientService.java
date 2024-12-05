//package com.prodify.apigateway.util;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Service
//public class WebClientService {
//
//    private final WebClient webClient;
//
//    public WebClientService(WebClient.Builder webClientBuilder) {
//        this.webClient = webClientBuilder.build();
//    }
//
//    public <T> Mono<T> post(String uri, Object body, String requestId, Class<T> responseType) {
//        log.info("[RequestId: {}] Sending POST request to URI: {}", requestId, uri);
//        return webClient.post()
//                .uri(uri)
//                .header("X-Request-Id", requestId)
//                .bodyValue(body)
//                .retrieve()
//                .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
//                        .flatMap(errorBody -> {
//                            log.error("[RequestId: {}] Error response: {}", requestId, errorBody);
//                            return Mono.error(new RuntimeException(errorBody));
//                        }))
//                .bodyToMono(responseType);
//    }
//
//    public <T> Mono<T> get(String uri, String requestId, Class<T> responseType) {
//        log.info("[RequestId: {}] Sending GET request to URI: {}", requestId, uri);
//        return webClient.get()
//                .uri(uri)
//                .header("X-Request-Id", requestId)
//                .retrieve()
//                .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
//                        .flatMap(errorBody -> {
//                            log.error("[RequestId: {}] Error response: {}", requestId, errorBody);
//                            return Mono.error(new RuntimeException(errorBody));
//                        }))
//                .bodyToMono(responseType);
//    }
//}
