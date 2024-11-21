package com.prodify.cqrs.OrderService.query.api.controller;

import com.prodify.cqrs.OrderService.command.api.util.RequestIdContext;
import com.prodify.cqrs.OrderService.query.api.dto.SearchRequest;  // Import SearchRequest from the correct package
import com.prodify.cqrs.OrderService.command.api.model.OrderRestModel;
import com.prodify.cqrs.OrderService.query.api.exception.OrderNotFoundException;
import com.prodify.cqrs.OrderService.query.api.handler.OrderQueryHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


import java.util.List;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderQueryController {

    private final OrderQueryHandler orderQueryHandler;
    private final RequestIdContext requestIdContext;

    @Autowired
    public OrderQueryController(OrderQueryHandler orderQueryHandler, RequestIdContext requestIdContext) {
        this.orderQueryHandler = orderQueryHandler;
        this.requestIdContext = requestIdContext;
    }

    // Get a specific order by orderId
    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderRestModel> getOrder(@PathVariable String orderId) {
        String requestId = requestIdContext.getRequestId();
        log.debug("Received request to fetch order details for orderId: {} with requestId: {}", orderId, requestId);
        try {
            OrderRestModel order = orderQueryHandler.getOrderById(orderId);
            if (order == null) {
                log.warn("Order with orderId: {} not found. RequestId: {}", orderId, requestId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            log.info("Successfully retrieved order details for orderId: {} with requestId: {}", orderId, requestId);
            return ResponseEntity.ok(order);
        }catch(OrderNotFoundException e){
            log.warn("Order with orderId: {} not found. RequestId: {}", orderId, requestId, e);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        catch (Exception e) {
            log.error("Error fetching order with orderId: {} and requestId: {}", orderId, requestId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get all orders with pagination and sorting
    @GetMapping("/orders")
    public ResponseEntity<List<OrderRestModel>> getAllOrders() {
        String requestId = requestIdContext.getRequestId();
        log.debug("Received request to fetch all orders with requestId: {}", requestId);
        try {
            List<OrderRestModel> orders = orderQueryHandler.getAllOrders();
            log.info("Successfully retrieved {} orders with requestId: {}", orders.size(), requestId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching all orders with requestId: {}", requestId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    // New POST endpoint for searching orders with pagination and sorting
    @PostMapping("/search")
    public ResponseEntity<List<OrderRestModel>> searchOrders(@RequestBody SearchRequest searchRequest,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "productId") String sortBy,
                                                             @RequestParam(defaultValue = "asc") String direction) {

        // Log the search request
        String requestId = requestIdContext.getRequestId();
        log.debug("Received search request with requestId: {}. Search criteria: {}, Page: {}, Size: {}, SortBy: {}, Direction: {}",
                requestId, searchRequest, page, size, sortBy, direction);

        // Validate that quantityMin is less than or equal to quantityMax
        if (searchRequest.getQuantityMin() != null && searchRequest.getQuantityMax() != null
                && searchRequest.getQuantityMin() > searchRequest.getQuantityMax()) {
            log.warn("Invalid search request: quantityMin cannot be greater than quantityMax. RequestId: {}", requestId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Set the sorting direction
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);

        // Create the Pageable object for pagination and sorting
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        try {
            // Perform the search with dynamic criteria and pagination
            List<OrderRestModel> orders = orderQueryHandler.searchOrders(
                    searchRequest.getProductId(), searchRequest.getUserId(), searchRequest.getAddressId(),
                    searchRequest.getQuantityMin(), searchRequest.getQuantityMax(), pageable);

            // Log success
            log.info("Successfully retrieved {} orders based on search criteria with requestId: {}", orders.size(), requestId);

            // Return the found orders with OK status
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            // Log error and return internal server error
            log.error("Error during order search with requestId: {}", requestId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
