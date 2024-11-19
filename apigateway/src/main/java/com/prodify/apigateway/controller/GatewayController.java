//package com.prodify.apigateway.controller;
//
//import com.prodify.apigateway.dto.OrderRestModel;
//import com.prodify.apigateway.dto.ProductRestModel;
//import com.prodify.apigateway.dto.UserDTO;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//@RestController
//@RequestMapping("/gateway")
//public class GatewayController {
//
//    private final WebClient webClient;
//
//    public GatewayController(WebClient webClient) {
//        this.webClient = webClient;
//    }
//
//    // Order Service Endpoints
//    @Tag(name = "Order Service", description = "Operations for managing orders")
//    @Operation(summary = "Create an Order", description = "Creates a new order by delegating to the Order Service")
//    @PostMapping("/orders")
//    public Mono<ResponseEntity<String>> createOrder(@RequestBody OrderRestModel orderRestModel) {
//        return webClient.post()
//                .uri("http://localhost:8083/orders/order")
//                .bodyValue(orderRestModel)
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        response -> response.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
//                .bodyToMono(String.class)
//                .map(ResponseEntity::ok)
//                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
//    }
//
//    @Tag(name = "Order Service", description = "Operations for managing orders")
//    @Operation(summary = "Get Order Message", description = "Returns a test message from the Order Service")
//    @GetMapping("/orders/message")
//    public Mono<ResponseEntity<String>> getOrderMessage() {
//        return webClient.get()
//                .uri("http://localhost:8083/orders/message")
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        response -> response.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
//                .bodyToMono(String.class)
//                .map(ResponseEntity::ok)
//                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
//    }
//
//    // Product Service Endpoints
//    @Tag(name = "Product Service", description = "Operations for managing products")
//    @Operation(summary = "Add a Product", description = "Adds a new product by delegating to the Product Service")
//    @PostMapping("/products")
//    public Mono<ResponseEntity<String>> addProduct(@RequestBody ProductRestModel productRestModel) {
//        return webClient.post()
//                .uri("http://localhost:8085/products")
//                .bodyValue(productRestModel)
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        response -> response.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
//                .bodyToMono(String.class)
//                .map(ResponseEntity::ok)
//                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
//    }
//
//    // User Service Endpoints
//    @Tag(name = "User Service", description = "Operations for managing users")
//    @Operation(summary = "Get User Payment Details", description = "Fetches payment details for a specific user from the User Service")
//    @GetMapping("/users/{userId}")
//    public Mono<ResponseEntity<UserDTO>> getUserPaymentDetails(@PathVariable String userId) {
//        return webClient.get()
//                .uri("http://localhost:8087/users/" + userId)
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        response -> response.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
//                .bodyToMono(UserDTO.class)
//                .map(ResponseEntity::ok)
//                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)));
//    }
//}


//package com.prodify.apigateway.controller;
//import com.prodify.apigateway.dto.OrderRestModel;
//import com.prodify.apigateway.dto.ProductRestModel;
//import com.prodify.apigateway.dto.UserDTO;
//import com.prodify.apigateway.util.ServiceName;
//import com.prodify.apigateway.util.UriBuilder;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//@RestController
//@RequestMapping("/gateway")
//public class GatewayController {
//
//    private final WebClient webClient;
//
//    public GatewayController(WebClient.Builder webClientBuilder) {
//        this.webClient = webClientBuilder.build();  // Build load-balanced WebClient
//    }
//
//    // Order Service Endpoints
//
//    @Tag(name = "Order Service", description = "Operations for managing orders")
//    @Operation(summary = "Create an Order", description = "Creates a new order by delegating to the Order Service")
//    @PostMapping("/orders")
//    public Mono<ResponseEntity<String>> createOrder(@RequestBody OrderRestModel orderRestModel) {
//
//        String uri = new UriBuilder(ServiceName.ORDER_SERVICE_URL, "order").build();
//        System.out.println("URI: " + uri);
//        return webClient.post()
//                .uri(uri)  // Using lb:// for load balancing
//                .bodyValue(orderRestModel)
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        response -> response.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
//                .bodyToMono(String.class)
//                .map(ResponseEntity::ok)
//                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
//    }
//
////    @Tag(name = "Order Service", description = "Operations for managing orders")
////    @Operation(summary = "Create an Order", description = "Creates a new order by delegating to the Order Service")
////    @PostMapping("/orders")
////    public Mono<ResponseEntity<String>> createOrder(@RequestBody OrderRestModel orderRestModel) {
////        String uri = new UriBuilder(ServiceName.ORDER_SERVICE_URL, "order").build();
////
////        return webClient.post()
////                .uri(uri)
////                .bodyValue(orderRestModel)
////                .retrieve()
////                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
////                        response -> response.bodyToMono(String.class)
////                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
////                .bodyToMono(String.class)
////                .map(ResponseEntity::ok)
////                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
////    }
//
//
//    @Tag(name = "Order Service", description = "Operations for managing orders")
//    @Operation(summary = "Get Order Message", description = "Returns a test message from the Order Service")
//    @GetMapping("/message")
//    public Mono<ResponseEntity<String>> getOrderMessage() {
//        String uri = new UriBuilder(ServiceName.ORDER_SERVICE_URL, "message").build();
//        return webClient.get()
//                .uri(uri)  // Using lb:// for load balancing //"lb://order-service/orders/message"
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        response -> response.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
//                .bodyToMono(String.class)
//                .map(ResponseEntity::ok)
//                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
//    }
//
//    // Product Service Endpoints
//
//    @Tag(name = "Product Service", description = "Operations for managing products")
//    @Operation(summary = "Add a Product", description = "Adds a new product by delegating to the Product Service")
//    @PostMapping("/products")
//    public Mono<ResponseEntity<String>> addProduct(@RequestBody ProductRestModel productRestModel) {
//        String uri = new UriBuilder(ServiceName.PRODUCT_SERVICE_URL, "product").build();
//
//        return webClient.post()
//                .uri(uri)  // Using lb:// for load balancing
//                .bodyValue(productRestModel)
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        response -> response.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
//                .bodyToMono(String.class)
//                .map(ResponseEntity::ok)
//                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
//    }
//
//    // User Service Endpoints
//
//    @Tag(name = "User Service", description = "Operations for managing users")
//    @Operation(summary = "Get User Payment Details", description = "Fetches payment details for a specific user from the User Service")
//    @GetMapping("/users/{userId}")
//    public Mono<ResponseEntity<UserDTO>> getUserPaymentDetails(@PathVariable String userId) {
//        String uri = new UriBuilder(ServiceName.USER_SERVICE_URL, userId).build();
//
//        return webClient.get()
//                .uri(uri)  // Using lb:// for load balancing
//                .retrieve()
//                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
//                        response -> response.bodyToMono(String.class)
//                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
//                .bodyToMono(UserDTO.class)
//                .map(ResponseEntity::ok)
//                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)));
//    }
//}

package com.prodify.apigateway.controller;

import com.prodify.apigateway.dto.OrderRestModel;
import com.prodify.apigateway.dto.ProductRestModel;
import com.prodify.apigateway.dto.SearchRequest;
import com.prodify.apigateway.dto.UserDTO;
import com.prodify.apigateway.util.RequestIdContext;
import com.prodify.apigateway.util.ServiceName;
import com.prodify.apigateway.util.UriBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/gateway")
@Slf4j
public class GatewayController {

    private final WebClient webClient;
    private final RequestIdContext requestIdContext;

    public GatewayController(WebClient.Builder webClientBuilder, RequestIdContext requestIdContext) {
        this.webClient = webClientBuilder.build(); // Build load-balanced WebClient
        this.requestIdContext = requestIdContext;  // Inject request tracking context
    }

    @PostMapping("/test-request-id")
    public Mono<String> testRequestId() {
        return requestIdContext.getRequestId()
                .map(requestId -> {
                    log.info("Received RequestId in controller: {}", requestId);
                    return "RequestId: " + requestId;
                });
    }
    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Create an Order", description = "Creates a new order by delegating to the Order Service")
    @PostMapping("/order")
    public Mono<ResponseEntity<String>> createOrder(@RequestBody OrderRestModel orderRestModel) {
        return requestIdContext.getRequestId()
                .flatMap(requestId -> {
                    String uri = UriBuilder.of(ServiceName.ORDER_SERVICE_URL, "order").build();
                    log.info("[RequestId: {}] Creating order with URI:{} and productId:{}", requestId, uri, orderRestModel.getProductId());

                    return webClient.post()
                            .uri(uri)
                            .header("X-Request-Id", requestId)
                            .bodyValue(orderRestModel)
                            .retrieve()
                            .onStatus(
                                    status -> status.is4xxClientError() || status.is5xxServerError(),
                                    response -> response.bodyToMono(String.class)
                                            .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                            .bodyToMono(String.class)
                            .map(response -> {
                                log.info("[RequestId: {}] Successfully created order", requestId);
                                return ResponseEntity.ok(response);
                            })
                            .doOnError(e -> log.error("[RequestId: {}] Error creating order: {}", requestId, e.getMessage(), e))
                            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
                });
    }

    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Get Order", description = "Fetches an order by ID")
    @GetMapping("/orders/{orderId}")
    public Mono<ResponseEntity<OrderRestModel>> getOrder(@PathVariable String orderId) {
        return requestIdContext.getRequestId()
                .flatMap(requestId -> {
                    String uri = UriBuilder.of(ServiceName.ORDER_SERVICE_URL, "order/" + orderId).build();
                    log.info("[RequestId: {}] Fetching order with ID:{} from URI:{}", requestId, orderId, uri);

                    return webClient.get()
                            .uri(uri)
                            .header("X-Request-Id", requestId)
                            .retrieve()
                            .onStatus(
                                    status -> status.is4xxClientError() || status.is5xxServerError(),
                                    response -> response.bodyToMono(String.class)
                                            .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                            .bodyToMono(OrderRestModel.class)
                            .map(response -> {
                                log.info("[RequestId: {}] Successfully fetched order ID: {}", requestId, orderId);
                                return ResponseEntity.ok(response);
                            })
                            .doOnError(e -> log.error("[RequestId: {}] Error fetching order {}: {}", requestId, orderId, e.getMessage(), e))
                            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)));
                });
    }
    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Get All Orders", description = "Fetches all orders through the gateway")
    @GetMapping("/orders")
    public Mono<ResponseEntity<List<OrderRestModel>>> getAllOrders() {
        return requestIdContext.getRequestId()
                .flatMap(requestId -> {
                    String uri = UriBuilder.of(ServiceName.ORDER_SERVICE_URL, "orders").build();
                    log.info("[RequestId: {}] Fetching all orders from URI: {}", requestId, uri);

                    return webClient.get()
                            .uri(uri)
                            .header("X-Request-Id", requestId)
                            .retrieve()
                            .onStatus(
                                    status -> status.is4xxClientError() || status.is5xxServerError(),
                                    response -> response.bodyToMono(String.class)
                                            .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                            .bodyToFlux(OrderRestModel.class)
                            .collectList()
                            .map(ResponseEntity::ok)
                            .doOnSuccess(response -> log.info("[RequestId: {}] Successfully fetched all orders", requestId))
                            .doOnError(e -> log.error("[RequestId: {}] Error fetching orders: {}", requestId, e.getMessage(), e))
                            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)));
                });
    }


    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Update an Order", description = "Updates an existing order by delegating to the Order Service")
    @PutMapping("/orders/{orderId}")
    public Mono<ResponseEntity<String>> updateOrder(@PathVariable String orderId, @RequestBody OrderRestModel orderRestModel) {
        return requestIdContext.getRequestId()
                .flatMap(requestId -> {
                    String uri = UriBuilder.of(ServiceName.ORDER_SERVICE_URL, "order/" + orderId).build();
                    log.info("[RequestId: {}] Updating order with ID: {} using URI: {}", requestId, orderId, uri);

                    return webClient.put()
                            .uri(uri)
                            .header("X-Request-Id", requestId)
                            .bodyValue(orderRestModel)
                            .retrieve()
                            .onStatus(
                                    status -> status.is4xxClientError() || status.is5xxServerError(),
                                    response -> response.bodyToMono(String.class)
                                            .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                            .bodyToMono(String.class)
                            .map(response -> {
                                log.info("[RequestId: {}] Successfully updated order ID: {}", requestId, orderId);
                                return ResponseEntity.ok(response);
                            })
                            .doOnError(e -> log.error("[RequestId: {}] Error updating order {}: {}", requestId, orderId, e.getMessage(), e))
                            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
                });
    }


    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Delete an Order", description = "Deletes an order by ID")
    @DeleteMapping("/orders/{orderId}")
    public Mono<ResponseEntity<String>> deleteOrder(@PathVariable String orderId) {
        return requestIdContext.getRequestId()
                .flatMap(requestId -> {
                    String uri = UriBuilder.of(ServiceName.ORDER_SERVICE_URL, "order/" + orderId).build();
                    log.info("[RequestId: {}] Deleting order with ID: {} from URI: {}", requestId, orderId, uri);

                    return webClient.delete()
                            .uri(uri)
                            .header("X-Request-Id", requestId)
                            .retrieve()
                            .onStatus(
                                    status -> status.is4xxClientError() || status.is5xxServerError(),
                                    response -> response.bodyToMono(String.class)
                                            .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                            .bodyToMono(String.class)
                            .map(response -> {
                                log.info("[RequestId: {}] Successfully deleted order ID: {}", requestId, orderId);
                                return ResponseEntity.ok(response);
                            })
                            .doOnError(e -> log.error("[RequestId: {}] Error deleting order {}: {}", requestId, orderId, e.getMessage(), e))
                            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
                });
    }

    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Search Orders", description = "Search orders by specific criteria with pagination and sorting")
    @PostMapping("/orders/search")
    public Mono<ResponseEntity<List<OrderRestModel>>> searchOrders(@RequestBody SearchRequest searchRequest) {
        return requestIdContext.getRequestId()
                .flatMap(requestId -> {
                    String uri = UriBuilder.of(ServiceName.ORDER_SERVICE_URL, "search").build();
                    log.info("[RequestId: {}] Searching orders with criteria: {}", requestId, searchRequest);

                    return webClient.post()
                            .uri(uri)
                            .header("X-Request-Id", requestId)
                            .bodyValue(searchRequest)
                            .retrieve()
                            .onStatus(
                                    status -> status.is4xxClientError() || status.is5xxServerError(),
                                    response -> response.bodyToMono(String.class)
                                            .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                            .bodyToFlux(OrderRestModel.class)
                            .collectList()
                            .map(ResponseEntity::ok)
                            .doOnSuccess(response -> log.info("[RequestId: {}] Successfully searched orders", requestId))
                            .doOnError(e -> log.error("[RequestId: {}] Error searching orders: {}", requestId, e.getMessage(), e))
                            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)));
                });
    }
    // Product Service Endpoints

    @Tag(name = "Product Service", description = "Operations for managing products")
    @Operation(summary = "Add a Product", description = "Adds a new product by delegating to the Product Service")
    @PostMapping("/products")
    public Mono<ResponseEntity<String>> addProduct(@RequestBody ProductRestModel productRestModel) {
        return requestIdContext.getRequestId()
                .flatMap(requestId -> {
                    String uri = UriBuilder.of(ServiceName.PRODUCT_SERVICE_URL, "product").build();
                    log.info("[RequestId: {}] Creating product with URI: {} and data: {}", requestId, uri, productRestModel);

                    return webClient.post()
                            .uri(uri)
                            .header("X-Request-Id", requestId)
                            .bodyValue(productRestModel)
                            .retrieve()
                            .onStatus(
                                    status -> status.is4xxClientError() || status.is5xxServerError(),
                                    response -> response.bodyToMono(String.class)
                                            .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                            .bodyToMono(String.class)
                            .map(response -> {
                                log.info("[RequestId: {}] Successfully created product", requestId);
                                return ResponseEntity.ok(response);
                            })
                            .doOnError(e -> log.error("[RequestId: {}] Error adding product: {}", requestId, e.getMessage(), e))
                            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage())));
                });
    }

    @Tag(name = "User Service", description = "Operations for managing users")
    @Operation(summary = "Get User Payment Details", description = "Fetches payment details for a specific user from the User Service")
    @GetMapping("/users/{userId}")
    public Mono<ResponseEntity<UserDTO>> getUserPaymentDetails(@PathVariable String userId) {
        return requestIdContext.getRequestId()
                .flatMap(requestId -> {
                    String uri = UriBuilder.of(ServiceName.USER_SERVICE_URL, userId).build();
                    log.info("[RequestId: {}] Fetching payment details for user ID: {} from URI: {}", requestId, userId, uri);

                    return webClient.get()
                            .uri(uri)
                            .header("X-Request-Id", requestId)
                            .retrieve()
                            .onStatus(
                                    status -> status.is4xxClientError() || status.is5xxServerError(),
                                    response -> response.bodyToMono(String.class)
                                            .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                            .bodyToMono(UserDTO.class)
                            .map(response -> {
                                log.info("[RequestId: {}] Successfully fetched payment details for user ID: {}", requestId, userId);
                                return ResponseEntity.ok(response);
                            })
                            .doOnError(e -> log.error("[RequestId: {}] Error fetching payment details for user {}: {}", requestId, userId, e.getMessage(), e))
                            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)));
                });
    }

}



