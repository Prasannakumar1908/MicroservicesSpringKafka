package com.prodify.apigateway.util;

import com.prodify.apigateway.exception.DownstreamServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class GatewayServiceDecoratorImpl implements GatewayServiceDecorator {

    private final WebClient webClient;
    private final RequestIdContext requestIdContext;

    public GatewayServiceDecoratorImpl(WebClient.Builder webClientBuilder, RequestIdContext requestIdContext) {
        this.webClient = webClientBuilder.build(); // Build WebClient from the builder
        this.requestIdContext = requestIdContext;
    }

    @Override
    public <T> Mono<ResponseEntity<T>> execute(String serviceName, String endpoint, HttpMethod method, Object body, ParameterizedTypeReference<T> responseType) {
        return requestIdContext.getRequestId()
                .flatMap(requestId -> {
                    String uri = UriBuilder.of(ServiceName.valueOf(serviceName), endpoint).build();
                    log.info("[RequestId: {}] Executing {} request to URI: {}", requestId, method, uri);
                    WebClient.RequestBodySpec requestSpec = webClient.method(method)
                            .uri(uri)
                            .header("X-Request-Id", requestId)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    if (body != null) {
                        requestSpec.bodyValue(body);
                    }
                    return requestSpec.retrieve()
                            .onStatus(
                                    status -> status.is4xxClientError() || status.is5xxServerError(),
                                    clientResponse -> clientResponse.bodyToMono(String.class)
                                            .flatMap(errorBody -> {
                                                log.error("[RequestId: {}] Downstream error: {}", requestId, errorBody);
                                                return Mono.error(new DownstreamServiceException(
                                                        clientResponse.statusCode(),
                                                        errorBody
                                                ));
                                            })
                            )
                            .bodyToMono(responseType)
                            .map(ResponseEntity::ok)
                            .doOnSuccess(response -> log.info("[RequestId: {}] Request successful", requestId))
                            .doOnError(e -> log.error("[RequestId: {}] Request failed: {}", requestId, e.getMessage(), e));
                });
    }
}