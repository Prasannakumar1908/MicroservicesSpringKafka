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
                .flatMap(originalRequestId -> {
                    // Use a mutable holder for the modified requestId
                    final String[] mutableRequestId = {originalRequestId};
                    // Extract productId dynamically if it exists in the request body
                    String productId = extractFieldFromBody(body, "productId");
                    if (productId != null) {
                        mutableRequestId[0] += "_productID_" + productId;
                    }
                    String uri = UriBuilder.of(ServiceName.valueOf(serviceName), endpoint).build();
                    log.info("[RequestId: {}] Executing {} request to URI: {}", mutableRequestId[0], method, uri);

                    WebClient.RequestBodySpec requestSpec = webClient.method(method)
                            .uri(uri)
                            .header("X-Request-Id", mutableRequestId[0])
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

                    if (body != null) {
                        requestSpec.bodyValue(body);
                    }

                    return requestSpec.retrieve()
                            .onStatus(
                                    status -> status.is4xxClientError() || status.is5xxServerError(),
                                    clientResponse -> clientResponse.bodyToMono(String.class)
                                            .flatMap(errorBody -> {
                                                log.error("[RequestId: {}] Downstream error: {}", mutableRequestId[0], errorBody);
                                                return Mono.error(new DownstreamServiceException(
                                                        clientResponse.statusCode(),
                                                        errorBody
                                                ));
                                            })
                            )

                            .bodyToMono(responseType)
                            .map(ResponseEntity::ok)
                            .doOnSuccess(response -> log.info("[RequestId: {}] Request successful", mutableRequestId[0]))
                            .doOnError(e -> log.error("[RequestId: {}] Request failed: {}", mutableRequestId[0], e.getMessage(), e));
                });
    }



    private String extractFieldFromBody(Object body, String fieldName) {
        if (body == null) {
            return null;
        }
        try {
            // Use reflection to find the field by name
            var field = body.getClass().getDeclaredField(fieldName);
            field.setAccessible(true); // Allow access to private fields
            Object value = field.get(body);
            return value != null ? value.toString() : null;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.warn("Field '{}' not found in the body object of type {}: {}", fieldName, body.getClass().getName(), e.getMessage());
            return null; // Return null if the field is not found or inaccessible
        }
    }

}