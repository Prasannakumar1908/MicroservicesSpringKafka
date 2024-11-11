package com.prodify.apigateway.controller;

import com.prodify.apigateway.dto.OrderRestModel;
import com.prodify.apigateway.dto.ProductRestModel;
import com.prodify.apigateway.dto.UserDTO;
import com.prodify.apigateway.util.ServiceName;
import com.prodify.apigateway.util.UriBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/gateway")
public class GatewayController {

    private final WebClient webClient;

    public GatewayController(WebClient webClient) {
        this.webClient = webClient;
    }

    // Order Service Endpoints

    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Create an Order", description = "Creates a new order by delegating to the Order Service")
    @PostMapping("/orders")
    public Mono<ResponseEntity<String>> createOrder(@RequestBody OrderRestModel orderRestModel) {
        String uri = new UriBuilder(ServiceName.ORDER_SERVICE_URL, "order").build();

        return webClient.post()
                .uri(uri)
                .bodyValue(orderRestModel)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
    }

    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Get Order Service Message", description = "Retrieves a test message from the Order Service")
    @GetMapping("/orders/message")
    public Mono<ResponseEntity<String>> getOrderMessage() {
        String uri = new UriBuilder(ServiceName.ORDER_SERVICE_URL, "message").build();

        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
    }

    // Product Service Endpoints

    @Tag(name = "Product Service", description = "Operations for managing products")
    @Operation(summary = "Add a Product", description = "Adds a new product by delegating to the Product Service")
    @PostMapping("/products")
    public Mono<ResponseEntity<String>> addProduct(@RequestBody ProductRestModel productRestModel) {
        String uri = new UriBuilder(ServiceName.PRODUCT_SERVICE_URL, "").build();

        return webClient.post()
                .uri(uri)
                .bodyValue(productRestModel)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
    }

    // User Service Endpoints

    @Tag(name = "User Service", description = "Operations for managing users")
    @Operation(summary = "Get User Payment Details", description = "Fetches payment details for a specific user from the User Service")
    @GetMapping("/users/{userId}")
    public Mono<ResponseEntity<UserDTO>> getUserPaymentDetails(@PathVariable String userId) {
        String uri = new UriBuilder(ServiceName.USER_SERVICE_URL, userId).build();

        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                .bodyToMono(UserDTO.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)));
    }
}
