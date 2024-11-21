package com.prodify.apigateway.controller;

import com.prodify.apigateway.dto.OrderRestModel;
import com.prodify.apigateway.dto.ProductRestModel;
import com.prodify.apigateway.dto.SearchRequest;
import com.prodify.apigateway.dto.UserDTO;
import com.prodify.apigateway.exception.BadRequestException;
import com.prodify.apigateway.exception.ResourceNotFoundException;
import com.prodify.apigateway.util.RequestIdContext;
import com.prodify.apigateway.util.ServiceName;
import com.prodify.apigateway.util.UriBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
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
        log.debug("Received request to create order: {}", orderRestModel);

        // Validation logic
        if (orderRestModel.getQuantity() <= 0) {
            log.warn("Validation failed: quantity must be greater than zero");
            throw new ValidationException("Quantity must be greater than zero");
        }

        return requestIdContext.getRequestId()
                .flatMap(requestId -> {
                    String uri = UriBuilder.of(ServiceName.ORDER_SERVICE_URL, "order").build();
                    log.info("[RequestId: {}] Creating order with URI:{} and productId:{}", requestId, uri, orderRestModel.getProductId());

                    // Make the actual request to the downstream service
                    return webClient.post()
                            .uri(uri)
                            .header("X-Request-Id", requestId)
                            .bodyValue(orderRestModel)
                            .retrieve()
                            .onStatus(
                                    status -> status.is4xxClientError(),  // Handle client-side errors
                                    response -> response.bodyToMono(String.class)
                                            .flatMap(errorBody -> {
                                                log.error("[RequestId: {}] Client error response from downstream service: {}", requestId, errorBody);
                                                return Mono.error(new BadRequestException("Client error: " + errorBody));  // Use a specific exception
                                            })
                            )
                            .onStatus(
                                    status -> status.is5xxServerError(),  // Handle server-side errors
                                    response -> response.bodyToMono(String.class)
                                            .flatMap(errorBody -> {
                                                log.error("[RequestId: {}] Server error response from downstream service: {}", requestId, errorBody);
                                                return Mono.error(new RuntimeException("Server error: " + errorBody));  // Use a specific exception for server error
                                            })
                            )
                            .bodyToMono(String.class)
                            .doOnSuccess(response -> log.info("[RequestId: {}] Successfully created order ", requestId))
                            .doOnError(e -> log.error("[RequestId: {}] Error creating order: {}", requestId, e.getMessage(), e))
                            .map(ResponseEntity::ok)
                            .onErrorResume(e -> {
                                if (e instanceof BadRequestException) {
                                    // Return a 400 response if it's a client error
                                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()));
                                } else if (e instanceof RuntimeException) {
                                    // Return a 500 response if it's a server error
                                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()));
                                } else {
                                    // Default to 500 for unexpected errors
                                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage()));
                                }
                            });
                });
    }


    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Get Order", description = "Fetches an order by ID")
    @GetMapping("/orders/{orderId}")
    public Mono<ResponseEntity<OrderRestModel>> getOrder(@PathVariable String orderId) {
        log.debug("Received request to fetch order with ID: {}", orderId);

        if (orderId == null || orderId.isEmpty()) {
            log.warn("Validation failed: Order ID must not be null or empty");
            throw new ValidationException("Order ID must not be null or empty");
        }

        return requestIdContext.getRequestId()
                .flatMap(requestId -> {
                    String uri = UriBuilder.of(ServiceName.ORDER_SERVICE_URL, "order/" + orderId).build();
                    log.info("[RequestId: {}] Fetching order with ID:{} from URI:{}", requestId, orderId, uri);

                    return webClient.get()
                            .uri(uri)
                            .header("X-Request-Id", requestId)
                            .retrieve()
                            .onStatus(
                                    status -> status.is4xxClientError(),
                                    response -> {
                                        log.warn("[RequestId: {}] Order with ID: {} not found", requestId, orderId);
                                        return Mono.error(new ResourceNotFoundException("Order not found"));
                                    })
                            .onStatus(
                                    status -> status.is5xxServerError(),
                                    response -> response.bodyToMono(String.class)
                                            .flatMap(errorBody -> {
                                                log.error("[RequestId: {}] Downstream error while fetching order: {}", requestId, errorBody);
                                                return Mono.error(new RuntimeException(errorBody));
                                            }))
                            .bodyToMono(OrderRestModel.class)
                            .map(response -> {
                                log.info("[RequestId: {}] Successfully fetched order ID: {}", requestId, orderId);
                                return ResponseEntity.ok(response);
                            })
                            .doOnError(e -> log.error("[RequestId: {}] Error fetching order ID {}: {}", requestId, orderId, e.getMessage(), e))
                            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)));
                });
    }
    // Get All Orders
    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Get All Orders", description = "Fetches all orders through the gateway")
    @GetMapping("/orders")
    public Mono<ResponseEntity<List<OrderRestModel>>> getAllOrders() {
        log.debug("Request to fetch all orders received");
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

    // Update Order
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

    // Delete Order
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

    // Search Orders
    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Search Orders", description = "Search orders by specific criteria with pagination and sorting")
    @PostMapping("/orders/search")
    public Mono<ResponseEntity<List<OrderRestModel>>> searchOrders(
            @RequestParam int page, // Pagination: page number
            @RequestParam int size, // Pagination: page size
            @RequestParam String sortBy, // Sorting: field to sort by
            @RequestParam String direction, // Sorting: direction (asc/desc)
            @RequestBody SearchRequest searchRequest) {

        // Retrieve the requestId from the context
        return requestIdContext.getRequestId()
                .flatMap(requestId -> {
                    // Construct a custom requestId for logging
                    String customRequestId = requestId + "_productId_" + (searchRequest.getProductId() != null ? searchRequest.getProductId() : "none");
                    log.debug("[RequestId: {}] Received search request. Criteria: {}", customRequestId, searchRequest);

                    // Validate quantity range
                    if (searchRequest.getQuantityMin() != null && searchRequest.getQuantityMax() != null
                            && searchRequest.getQuantityMin() > searchRequest.getQuantityMax()) {
                        log.warn("[RequestId: {}] Invalid quantity range. quantityMin cannot be greater than quantityMax.", customRequestId);
                        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null));
                    }

                    // Validate and sanitize direction
                    Sort.Direction sortDirection;
                    try {
                        sortDirection = Sort.Direction.fromString(direction.trim());
                    } catch (IllegalArgumentException e) {
                        log.warn("[RequestId: {}] Invalid sort direction '{}'. Defaulting to 'asc'.", customRequestId, direction);
                        sortDirection = Sort.Direction.ASC; // Default to ascending if invalid
                    }

                    // Build the URI for the downstream service using UriBuilder
                    String uri = UriBuilder.of(ServiceName.ORDER_SERVICE_URL, "search")
                            .addQueryParam("page", String.valueOf(page))
                            .addQueryParam("size", String.valueOf(size))
                            .addQueryParam("sortBy", sortBy)
                            .addQueryParam("direction", sortDirection.name().toLowerCase())
                            .build();

                    log.debug("[RequestId: {}] Making request to Order Service: {}", customRequestId, uri);

                    // Perform the POST request to the downstream service
                    return webClient.post()
                            .uri(uri)
                            .header("X-Request-Id", customRequestId)
                            .bodyValue(searchRequest) // Pass the request body as is
                            .retrieve()
                            .onStatus(
                                    status -> status.is4xxClientError() || status.is5xxServerError(),
                                    response -> response.bodyToMono(String.class)
                                            .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody))))
                            .bodyToFlux(OrderRestModel.class) // Deserialize response
                            .collectList()
                            .map(ResponseEntity::ok) // Wrap the response in ResponseEntity with OK status
                            .doOnSuccess(response -> log.info("[RequestId: {}] Successfully retrieved {} orders.", customRequestId, response.getBody().size()))
                            .doOnError(e -> log.error("[RequestId: {}] Error searching orders: {}", customRequestId, e.getMessage(), e))
                            .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)));
                });
    }


    // Product Service Endpoints

    @Tag(name = "Product Service", description = "Operations for managing products")
    @Operation(summary = "Add a Product", description = "Adds a new product by delegating to the Product Service")
    @PostMapping("/products")
    public Mono<ResponseEntity<String>> addProduct(@RequestBody ProductRestModel productRestModel) {
        log.debug("Received request to add a new product: {}", productRestModel);

        // Optional validation for product fields
        if (productRestModel.getName() == null || productRestModel.getName().isEmpty()) {
            log.warn("Validation failed: Product name is required");
            throw new ValidationException("Product name is required");
        }

        if (productRestModel.getPrice() == null || productRestModel.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Validation failed: Product price must be greater than zero");
            throw new ValidationException("Product price must be greater than zero");
        }

        return requestIdContext.getRequestId()
                .flatMap(requestId -> {
                    String uri = UriBuilder.of(ServiceName.PRODUCT_SERVICE_URL, "product").build();
                    log.info("[RequestId: {}] Adding new product with URI: {} and data: {}", requestId, uri, productRestModel);

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
                                log.info("[RequestId: {}] Successfully added product", requestId);
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
        log.debug("Received request to fetch payment details for user ID: {}", userId);

        if (userId == null || userId.isEmpty()) {
            log.warn("Validation failed: User ID is required");
            throw new ValidationException("User ID is required");
        }

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



