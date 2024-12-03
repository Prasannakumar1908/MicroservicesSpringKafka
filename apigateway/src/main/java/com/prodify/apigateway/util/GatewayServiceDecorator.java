package com.prodify.apigateway.util;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface GatewayServiceDecorator {
    <T> Mono<ResponseEntity<T>> execute(String serviceName, String endpoint, HttpMethod method, Object body, ParameterizedTypeReference<T> responseType);
}
