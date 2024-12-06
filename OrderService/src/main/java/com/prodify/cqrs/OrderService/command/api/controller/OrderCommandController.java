package com.prodify.cqrs.OrderService.command.api.controller;

import com.prodify.cqrs.OrderService.command.api.command.CreateOrderCommand;
import com.prodify.cqrs.OrderService.command.api.command.DeleteOrderCommand;
import com.prodify.cqrs.OrderService.command.api.command.UpdateOrderCommand;
import com.prodify.cqrs.OrderService.command.api.kafka.OrderKafkaProducer;
import com.prodify.cqrs.OrderService.command.api.model.OrderRestModel;
import com.prodify.cqrs.OrderService.command.api.util.RequestIdContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@Slf4j
@Validated
public class OrderCommandController {

    private final CommandGateway commandGateway;
    private final OrderKafkaProducer orderKafkaProducer;
    private final RequestIdContext requestIdContext;
    @Autowired
    public OrderCommandController(CommandGateway commandGateway, OrderKafkaProducer orderKafkaProducer, RequestIdContext requestIdContext) {
        this.commandGateway = commandGateway;
        this.orderKafkaProducer = orderKafkaProducer;
        this.requestIdContext = requestIdContext;
    }
    // Create Order Endpoint
    @PostMapping("/order")
    public ResponseEntity<String> createOrder(
            @Valid @RequestBody OrderRestModel orderRestModel) {
        String requestId = requestIdContext.getRequestId();
        String orderId = UUID.randomUUID().toString();
        orderRestModel.setOrderId(orderId);
        orderRestModel.setRequestId(requestId);
        log.debug("Received CreateOrder request with requestId: {} and details: {} using request /orders/order to orderservice", requestId, orderRestModel);
        if (orderRestModel.getQuantity() <= 0) {
            log.warn("Invalid quantity {} for productId {} in requestId: {}. Quantity must be greater than 0.",
                    orderRestModel.getQuantity(), orderRestModel.getProductId(), requestId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid quantity. Must be greater than 0.");
        }
        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder().orderId(orderId).addressId(orderRestModel.getAddressId())
                .userId(orderRestModel.getUserId()).productId(orderRestModel.getProductId()).quantity(orderRestModel.getQuantity()).orderStatus("CREATED").requestId(requestId).build();
        try {
            log.debug("Sending CreateOrderCommand to command gateway for Order ID:{} with request Id:{}",orderId,requestId);
            commandGateway.sendAndWait(createOrderCommand);
            log.info("Successfully sent CreateOrderCommand for Order ID: {} with request ID:{}", orderId, requestId);
            // Publish event to Kafka
//            String orderData = orderRestModel.toString();
            orderKafkaProducer.sendOrderEvent("order-events",orderId, orderRestModel);
            log.info("Order event successfully published to Kafka for Order ID: {} with requestID:{}", orderId, requestId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Order Created with ID: " + orderId);
        } catch (CommandExecutionException e) {
            log.error("Failed to execute CreateOrderCommand for Order ID: {} with requestId:{}", orderId,requestId, e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error creating order. Please try again later.");
        }catch (Exception e) {
            log.error("Unexpected error occurred while creating order with requestId: {}", requestId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error occurred. Please try again later.");
        }
    }
    // Update Order Endpoint
    @PutMapping("/order/{orderId}")
    public ResponseEntity<String> updateOrder(
            @PathVariable("orderId") String orderId,
            @Valid @RequestBody OrderRestModel orderRestModel) {

        String requestId = requestIdContext.getRequestId();
        log.debug("Received UpdateOrder request for Order ID: {} with requestId: {} using request /orders/order/{}", orderId, requestId,orderId);
        if(orderRestModel.getQuantity() <= 0) {
            log.warn("Invalid quantity:{} for productId:{} in Updateorder request with requestId:{}, Quantity must be greater than 0.",orderRestModel.getQuantity(), orderRestModel.getProductId(), requestId
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid quantity. Must be greater than 0.");
        }
        try{
            UpdateOrderCommand updateOrderCommand = UpdateOrderCommand.builder()
                .orderId(orderId)
                .userId(orderRestModel.getUserId())
                .addressId(orderRestModel.getAddressId())
                .productId(orderRestModel.getProductId())
                .quantity(orderRestModel.getQuantity())
                .orderStatus("UPDATED")
                .requestId(requestId)
                .build();
            log.debug("Sending UpdateOrderCommand to command gateway for Order ID: {} with requestID: {}",orderId,requestId);
            commandGateway.sendAndWait(updateOrderCommand);
            log.info("Successfully sent UpdateOrderCommand for Order ID: {}", orderId);
            return ResponseEntity.ok("Order Updated with ID: " + orderId);

        } catch (CommandExecutionException e) {
            log.error("Failed to execute UpdateOrderCommand for Order ID: {} with requestId:{}", orderId,requestId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error updating order. Please try again later.");
        }catch (Exception e) {
            log.error("Unexpected error occurred while updating order with requestId: {}", requestId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error occurred. Please try again later.");
        }
    }
    // Delete Order Endpoint
    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<String> deleteOrder(
            @PathVariable String orderId) {

        String requestId = requestIdContext.getRequestId();
        log.debug("Received DeleteOrder request for Order ID: {} with requestId: {} using /orders/order/{}", orderId, requestId,orderId);
//
//        try {
//            UUID.fromString(orderId);
//        } catch (IllegalArgumentException e) {
//            log.warn("Invalid Order ID format: {}. Expected UUID format in requestId: {}", orderId, requestId);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Order ID format.");
//        }

        DeleteOrderCommand deleteOrderCommand = DeleteOrderCommand.builder()
                .orderId(orderId)
                .requestId(requestId)
                .build();

        try {
            log.debug("Sending DeleteOrderCommand to command gateway for Order ID: {} with requestId:{}", orderId,requestId);
            commandGateway.sendAndWait(deleteOrderCommand);
            log.info("Successfully sent DeleteOrderCommand for Order ID: {}", orderId);
            return ResponseEntity.ok("Order Deleted with ID: " + orderId);

        } catch (CommandExecutionException e) {
            log.error("Failed to execute DeleteOrderCommand for Order ID: {} with requestId:{}", orderId,requestId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error deleting order. Please try again later.");
        }catch (Exception e) {
            log.error("Unexpected error occurred while deleting order with requestId: {}", requestId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error occurred. Please try again later.");
        }
    }
}

