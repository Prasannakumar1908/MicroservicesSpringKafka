////package com.prodify.cqrs.OrderService.command.api.controller;
////
////import com.prodify.cqrs.OrderService.command.api.command.CreateOrderCommand;
////import com.prodify.cqrs.OrderService.command.api.kafka.OrderKafkaProducer;
////import com.prodify.cqrs.OrderService.command.api.model.OrderRestModel;
//////import io.swagger.v3.oas.annotations.Operation;
//////import io.swagger.v3.oas.annotations.tags.Tag;
////import lombok.extern.slf4j.Slf4j;
////import org.axonframework.commandhandling.gateway.CommandGateway;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.web.bind.annotation.*;
////
////import java.util.UUID;
////
////@RestController
////@RequestMapping("/orders")
//////@Tag(name = "Order Command Controller", description = "Operations related to Order")
////@Slf4j
////public class OrderCommandController {
////
////    private CommandGateway commandGateway;
////
////    private final OrderKafkaProducer orderKafkaProducer;
////
////
////    @Autowired
////    public OrderCommandController(CommandGateway commandGateway, OrderKafkaProducer orderKafkaProducer) {
////        this.commandGateway = commandGateway;
////        this.orderKafkaProducer = orderKafkaProducer;
////    }
////
////    @GetMapping("/message")
//////    @Operation(summary = "Test Order Service", description = "Returns a test message from Order Service")
////    public String getMessage(){
////        log.info("Received request for test message");
////        return "Reached orderservice";
////    }
////
////    @PostMapping("/order")
//////    @Operation(summary = "Create an Order", description = "Creates a new order with the provided details")
////    public String createOrder(@RequestBody OrderRestModel orderRestModel) {
////        log.info("Received CreateOrder request with details: {}", orderRestModel.getProductId());
////
////        String orderId = UUID.randomUUID().toString();
////
////        CreateOrderCommand createOrderCommand
////                = CreateOrderCommand.builder()
////                .orderId(orderId)
////                .addressId(orderRestModel.getAddressId())
////                .productId(orderRestModel.getProductId())
////                .quantity(orderRestModel.getQuantity())
////                .orderStatus("CREATED")
////                .build();
////
////        commandGateway.sendAndWait(createOrderCommand);
////        log.info("Sent CreateOrderCommand for Order ID: {}", orderId);
////        String orderData = orderRestModel.toString();  // Customize as per your model
////        orderKafkaProducer.sendOrderEvent("order-events", orderData);
////
////        return "Order Created with ID: " + orderId;
////    }
////}
//
//package com.prodify.cqrs.OrderService.command.api.controller;
//
//import com.prodify.cqrs.OrderService.command.api.command.CreateOrderCommand;
//import com.prodify.cqrs.OrderService.command.api.command.DeleteOrderCommand;
//import com.prodify.cqrs.OrderService.command.api.command.UpdateOrderCommand;
//import com.prodify.cqrs.OrderService.command.api.kafka.OrderKafkaProducer;
//import com.prodify.cqrs.OrderService.command.api.model.OrderRestModel;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.validation.Valid;
//import lombok.extern.slf4j.Slf4j;
//import org.axonframework.commandhandling.CommandExecutionException;
//import org.axonframework.commandhandling.gateway.CommandGateway;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/orders")
//@Slf4j
//@Validated
//public class OrderCommandController {
//
//    private final CommandGateway commandGateway;
//    private final OrderKafkaProducer orderKafkaProducer;
//
//    @Autowired
//    public OrderCommandController(CommandGateway commandGateway, OrderKafkaProducer orderKafkaProducer) {
//        this.commandGateway = commandGateway;
//        this.orderKafkaProducer = orderKafkaProducer;
//    }
//
//    private String getRequestId(HttpServletRequest request) {
//        String requestId = request.getHeader("X-Request-ID");
//        if (requestId == null) {
//            // Generate a new requestId if none exists
//            requestId = UUID.randomUUID().toString();
//        }
//        return requestId;
//    }
//
//
//    @GetMapping("/message")
//    public ResponseEntity<String> getMessage(HttpServletRequest request) {
//        String requestId = getRequestId(request);
//        log.debug("Debug: Received request for test message with requestId {}", requestId);
//        return ResponseEntity.ok("Reached orderservice");
//
//    }
//
//    @PostMapping("/order")
//    public ResponseEntity<String> createOrder(@Valid @RequestBody OrderRestModel orderRestModel, @RequestHeader("X-Request-ID") String requestId ) {
//        log.info("Received craete order request with requestID:{}",requestId);
//
//        log.debug("Received CreateOrder request with details: {} and with requestID: {}", orderRestModel.getProductId(), requestId);
//
//        String orderId = UUID.randomUUID().toString();
//
//        if(orderRestModel.getQuantity()<=0){
//            log.warn("Warn: Invalid quantity{} for productId:{} requestId: {}, expected quantity to be greater than 0.", orderRestModel.getQuantity(), orderRestModel.getProductId(),requestId);
//
//        }
//
//        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
//                .orderId(orderId)
//                .addressId(orderRestModel.getAddressId())
//                .userId(orderRestModel.getUserId())
//                .productId(orderRestModel.getProductId())
//                .quantity(orderRestModel.getQuantity())
//                .orderStatus("CREATED")
//                .build();
//
//        try {
//            // Send the command to the command gateway
//            commandGateway.sendAndWait(createOrderCommand);
//            log.info("Successfully sent CreateOrderCommand for Order ID: {}", orderId);
//
//            // Prepare data for Kafka and send
//            String orderData = orderRestModel.toString();  // Customize to match your model's `toString()` implementation
//            orderKafkaProducer.sendOrderEvent("order-events", orderData);
//            log.info("Order event successfully published to Kafka for Order ID: {}", orderId);
//
//            return ResponseEntity.ok("Order Created with ID: " + orderId);
//
//        } catch (CommandExecutionException e) {
//            log.error("Failed to execute CreateOrderCommand for Order ID: {}", orderId, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error creating order. Please try again later.");
//
//        } catch (Exception e) {
//            log.error("Unexpected error while creating order for Order ID: {}", orderId, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Unexpected error occurred. Please contact support.");
//        }
//
//        finally {
//            // Clear MDC after request processing
//            MDC.clear();
//        }
//
//    }
//
//    @PutMapping("/order/{orderId}")
//    public ResponseEntity<String> updateOrder(@PathVariable("orderId") String orderId, @Valid @RequestBody OrderRestModel orderRestModel, HttpServletRequest request) {
//
//        String requestId = getRequestId(request);
//
//        MDC.put("X-Request-ID", requestId);
//        log.debug("Debug: Received UpdateOrder request for Order ID: {}, with requestId: {}", orderId, requestId);
//        UpdateOrderCommand updateOrderCommand = UpdateOrderCommand.builder()
//                .orderId(orderId)
//                .userId(orderRestModel.getUserId())
//                .addressId(orderRestModel.getAddressId())
//                .productId(orderRestModel.getProductId())
//                .quantity(orderRestModel.getQuantity())
//                .orderStatus("UPDATED")
//                .build();
//
//        try {
//            commandGateway.sendAndWait(updateOrderCommand);
//            log.info("Successfully sent UpdateOrderCommand for Order ID: {}", orderId);
//            return ResponseEntity.ok("Order Updated with ID: " + orderId);
//
//        } catch (CommandExecutionException e) {
//            log.error("Failed to execute UpdateOrderCommand for Order ID: {}", orderId, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error updating order. Please try again later.");
//
//        } catch (Exception e) {
//            log.error("Unexpected error while updating order for Order ID: {}", orderId, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Unexpected error occurred. Please contact support.");
//        }finally {
//            MDC.clear();
//        }
//    }
//
//    @DeleteMapping("/order/{orderId}")
//    public ResponseEntity<String> deleteOrder(@PathVariable String orderId, HttpServletRequest request) {
//
//        String requestId = getRequestId(request);
//
//        MDC.put("X-Request-ID", requestId);
//
//
//
//        log.debug("Debug: Received DeleteOrder request for Order ID: {} with requestId: {}", orderId, requestId);
//
//        try{
//            UUID.fromString(orderId);
//        } catch(IllegalArgumentException e){
//            log.warn("Warn: Invalid Order ID format received:{}. Expected valid UUID format, requestId:{}", orderId,requestId);
//        }
//
//        DeleteOrderCommand deleteOrderCommand = DeleteOrderCommand.builder()
//                .orderId(orderId)
//                .build();
//
//        try {
//            commandGateway.sendAndWait(deleteOrderCommand);
//            log.info("Successfully sent DeleteOrderCommand for Order ID: {}", orderId);
//            return ResponseEntity.ok("Order Deleted with ID: " + orderId);
//
//        } catch (CommandExecutionException e) {
//            log.error("Failed to execute DeleteOrderCommand for Order ID: {}", orderId, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error deleting order. Please try again later.");
//
//        } catch (Exception e) {
//            log.error("Unexpected error while deleting order for Order ID: {}", orderId, e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Unexpected error occurred. Please contact support.");
//        }finally {
//            MDC.clear();
//        }
//    }
//
//}
//

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

        log.info("Received CreateOrder request with requestId: {} and details: {} using request /orders/order to orderservice", requestId, orderRestModel);



        if (orderRestModel.getQuantity() <= 0) {
            log.warn("Invalid quantity {} for productId {} in requestId: {}. Quantity must be greater than 0.",
                    orderRestModel.getQuantity(), orderRestModel.getProductId(), requestId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid quantity. Must be greater than 0.");
        }

        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .orderId(orderId)
                .addressId(orderRestModel.getAddressId())
                .userId(orderRestModel.getUserId())
                .productId(orderRestModel.getProductId())
                .quantity(orderRestModel.getQuantity())
                .orderStatus("CREATED")
                .requestId(requestId)
                .build();

        try {
            commandGateway.sendAndWait(createOrderCommand);
            log.info("Successfully sent CreateOrderCommand for Order ID: {} with request ID:{}", orderId, requestId);

            // Publish event to Kafka
//            String orderData = orderRestModel.toString();
            orderKafkaProducer.sendOrderEvent("order-events",orderId, orderRestModel);
            log.info("Order event successfully published to Kafka for Order ID: {} with requestID:{}", orderId, requestId);

            return ResponseEntity.ok("Order Created with ID: " + orderId);

        } catch (CommandExecutionException e) {
            log.error("Failed to execute CreateOrderCommand for Order ID: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating order. Please try again later.");
        }
    }

    // Update Order Endpoint
    @PutMapping("/order/{orderId}")
    public ResponseEntity<String> updateOrder(
            @PathVariable("orderId") String orderId,
            @Valid @RequestBody OrderRestModel orderRestModel) {

        String requestId = requestIdContext.getRequestId();
        log.debug("Received UpdateOrder request for Order ID: {} with requestId: {} using request /orders/order/{}", orderId, requestId,orderId);

        UpdateOrderCommand updateOrderCommand = UpdateOrderCommand.builder()
                .orderId(orderId)
                .userId(orderRestModel.getUserId())
                .addressId(orderRestModel.getAddressId())
                .productId(orderRestModel.getProductId())
                .quantity(orderRestModel.getQuantity())
                .orderStatus("UPDATED")
                .requestId(requestId)
                .build();

        try {
            commandGateway.sendAndWait(updateOrderCommand);
            log.info("Successfully sent UpdateOrderCommand for Order ID: {}", orderId);
            return ResponseEntity.ok("Order Updated with ID: " + orderId);

        } catch (CommandExecutionException e) {
            log.error("Failed to execute UpdateOrderCommand for Order ID: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating order. Please try again later.");
        }
    }

    // Delete Order Endpoint
    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<String> deleteOrder(
            @PathVariable String orderId) {

        String requestId = requestIdContext.getRequestId();
        log.debug("Received DeleteOrder request for Order ID: {} with requestId: {} using /orders/order/{}", orderId, requestId,orderId);

        try {
            UUID.fromString(orderId);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid Order ID format: {}. Expected UUID format in requestId: {}", orderId, requestId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Order ID format.");
        }

        DeleteOrderCommand deleteOrderCommand = DeleteOrderCommand.builder()
                .orderId(orderId)
                .requestId(requestId)
                .build();

        try {
            commandGateway.sendAndWait(deleteOrderCommand);
            log.info("Successfully sent DeleteOrderCommand for Order ID: {}", orderId);
            return ResponseEntity.ok("Order Deleted with ID: " + orderId);

        } catch (CommandExecutionException e) {
            log.error("Failed to execute DeleteOrderCommand for Order ID: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting order. Please try again later.");
        }
    }
}

