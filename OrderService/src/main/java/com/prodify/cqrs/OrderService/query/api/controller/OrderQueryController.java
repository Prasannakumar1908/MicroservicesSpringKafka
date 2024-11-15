package com.prodify.cqrs.OrderService.query.api.controller;

import com.prodify.cqrs.OrderService.query.api.dto.SearchRequest;  // Import SearchRequest from the correct package
import com.prodify.cqrs.OrderService.command.api.model.OrderRestModel;
import com.prodify.cqrs.OrderService.query.api.handler.OrderQueryHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderQueryController {

    private final OrderQueryHandler orderQueryHandler;

    @Autowired
    public OrderQueryController(OrderQueryHandler orderQueryHandler) {
        this.orderQueryHandler = orderQueryHandler;
    }

    // Get a specific order by orderId
    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderRestModel> getOrder(@PathVariable String orderId) {
        try {
            OrderRestModel order = orderQueryHandler.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("Error fetching order with ID: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get all orders with pagination and sorting
    @GetMapping("/orders")
    public ResponseEntity<List<OrderRestModel>> getAllOrders() {
        try {
            List<OrderRestModel> orders = orderQueryHandler.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error fetching orders", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    // New POST endpoint for searching orders with pagination and sorting
    @PostMapping("/search")
    public ResponseEntity<List<OrderRestModel>> searchOrders(@RequestBody SearchRequest searchRequest) {
        log.info("Received search request: {}", searchRequest);

        try {
            List<OrderRestModel> orders = orderQueryHandler.searchOrders(
                    searchRequest.getProductId(),
                    searchRequest.getUserId(),
                    searchRequest.getAddressId(),
                    searchRequest.getQuantityMin(),
                    searchRequest.getQuantityMax(),
                    searchRequest.getPage(),
                    searchRequest.getSize(),
                    searchRequest.getSortBy(),
                    searchRequest.getDirection()
            );
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            log.error("Error during order search", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
