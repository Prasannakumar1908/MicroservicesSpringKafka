package com.prodify.apigateway.controller;

import com.prodify.apigateway.Model.*;
import com.prodify.apigateway.exception.BadRequestException;
import com.prodify.apigateway.exception.QuantityZeroFoundException;
import com.prodify.apigateway.exception.ServerErrorException;
import com.prodify.apigateway.exception.OrderNotValidException;
import com.prodify.apigateway.util.GatewayServiceDecorator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/gateway")
@Slf4j
public class GatewayController {

    private final GatewayServiceDecorator gatewayServiceDecorator;

    public GatewayController(GatewayServiceDecorator gatewayServiceDecorator) {
        this.gatewayServiceDecorator = gatewayServiceDecorator;
    }
    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Create an Order", description = "Create an order with a payload")
    @PostMapping("/order")
    public Mono<ResponseEntity<String>> createOrder(@RequestBody @Valid OrderRestModel orderRestModel) {
        log.info("Creating Order with details: {}", orderRestModel);
        return gatewayServiceDecorator.execute("ORDER_SERVICE_URL", "order", HttpMethod.POST, orderRestModel, new ParameterizedTypeReference<String>() {});
    }
    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Get Order", description = "Fetches an order by ID")
    @GetMapping("/order/{orderId}")
    public Mono<ResponseEntity<OrderRestModel>> getOrder(@PathVariable String orderId) {
        log.info("Getting Order with ID: {}", orderId);
        return gatewayServiceDecorator.execute("ORDER_SERVICE_URL", "order/" + orderId, HttpMethod.GET, null, new ParameterizedTypeReference<OrderRestModel>() {});
    }

    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Get All Orders", description = "Fetches all orders through the gateway")
    @GetMapping("/orders")
    public Mono<ResponseEntity<List<OrderRestModel>>> getAllOrders() {
        log.info("Fetching All Orders");
        return gatewayServiceDecorator.execute("ORDER_SERVICE_URL", "orders", HttpMethod.GET, null, new ParameterizedTypeReference<List<OrderRestModel>>() {});
    }

    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Update an Order", description = "Updates an existing order")
    @PutMapping("/order/{orderId}")
    public Mono<ResponseEntity<String>> updateOrder(@PathVariable String orderId, @RequestBody @Valid OrderRestModel orderRestModel) {
        log.info("Updating Order with ID: {} with details: {}", orderId, orderRestModel);
        return gatewayServiceDecorator.execute("ORDER_SERVICE_URL", "order/" + orderId, HttpMethod.PUT, orderRestModel, new ParameterizedTypeReference<String>() {});
    }

    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Delete an Order", description = "Deletes an order by ID")
    @DeleteMapping("/order/{orderId}")
    public Mono<ResponseEntity<String>> deleteOrder(@PathVariable String orderId) {
        log.info("Deleting Order with ID: {}", orderId);
        return gatewayServiceDecorator.execute("ORDER_SERVICE_URL", "order/" + orderId, HttpMethod.DELETE, null, new ParameterizedTypeReference<String>() {});
    }

    @Tag(name = "Product Service", description = "Operations for managing products")
    @Operation(summary = "Add a Product", description = "Adds a new product by delegating to the Product Service")
    @PostMapping("/product")
    public Mono<ResponseEntity<String>> addProduct(@RequestBody ProductRestModel productRestModel) {
        log.info("Adding Product: {}", productRestModel);
        return gatewayServiceDecorator.execute("PRODUCT_SERVICE_URL", "product", HttpMethod.POST, productRestModel, new ParameterizedTypeReference<String>() {});
    }

    @Tag(name = "User Service", description = "Operations for managing users")
    @Operation(summary = "Get User Payment Details", description = "Fetches payment details for a specific user")
    @GetMapping("/users/{userId}")
    public Mono<ResponseEntity<UserModel>> getUserPaymentDetails(@PathVariable String userId) {
        log.info("Fetching User Payment Details for User ID: {}", userId);
        return gatewayServiceDecorator.execute("USER_SERVICE_URL", userId, HttpMethod.GET, null, new ParameterizedTypeReference<UserModel>() {});
    }

    @Tag(name = "Order Service", description = "Operations for managing orders")
    @Operation(summary = "Search Orders", description = "Search for orders based on criteria")
    @PostMapping("/search")
    public Mono<ResponseEntity<List<OrderRestModel>>> searchOrders(@RequestBody SearchRequest searchRequest, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productId") String sortBy, @RequestParam(defaultValue = "asc") String direction) {
        if (searchRequest.getQuantityMin() != null && searchRequest.getQuantityMax() != null
                && searchRequest.getQuantityMin() > searchRequest.getQuantityMax()) {
            throw new BadRequestException("Invalid quantity range: min cannot be greater than max");
        }
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        log.info("Searching for orders with criteria: {}", searchRequest);
        return gatewayServiceDecorator.execute("ORDER_SERVICE_URL", "search", HttpMethod.POST, searchRequest, new ParameterizedTypeReference<List<OrderRestModel>>() {});
    }
}
