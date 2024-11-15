//package com.prodify.cqrs.OrderService.command.api.controller;
//
//import com.prodify.cqrs.OrderService.command.api.command.CreateOrderCommand;
//import com.prodify.cqrs.OrderService.command.api.kafka.OrderKafkaProducer;
//import com.prodify.cqrs.OrderService.command.api.model.OrderRestModel;
////import io.swagger.v3.oas.annotations.Operation;
////import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.extern.slf4j.Slf4j;
//import org.axonframework.commandhandling.gateway.CommandGateway;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/orders")
////@Tag(name = "Order Command Controller", description = "Operations related to Order")
//@Slf4j
//public class OrderCommandController {
//
//    private CommandGateway commandGateway;
//
//    private final OrderKafkaProducer orderKafkaProducer;
//
//
//    @Autowired
//    public OrderCommandController(CommandGateway commandGateway, OrderKafkaProducer orderKafkaProducer) {
//        this.commandGateway = commandGateway;
//        this.orderKafkaProducer = orderKafkaProducer;
//    }
//
//    @GetMapping("/message")
////    @Operation(summary = "Test Order Service", description = "Returns a test message from Order Service")
//    public String getMessage(){
//        log.info("Received request for test message");
//        return "Reached orderservice";
//    }
//
//    @PostMapping("/order")
////    @Operation(summary = "Create an Order", description = "Creates a new order with the provided details")
//    public String createOrder(@RequestBody OrderRestModel orderRestModel) {
//        log.info("Received CreateOrder request with details: {}", orderRestModel.getProductId());
//
//        String orderId = UUID.randomUUID().toString();
//
//        CreateOrderCommand createOrderCommand
//                = CreateOrderCommand.builder()
//                .orderId(orderId)
//                .addressId(orderRestModel.getAddressId())
//                .productId(orderRestModel.getProductId())
//                .quantity(orderRestModel.getQuantity())
//                .orderStatus("CREATED")
//                .build();
//
//        commandGateway.sendAndWait(createOrderCommand);
//        log.info("Sent CreateOrderCommand for Order ID: {}", orderId);
//        String orderData = orderRestModel.toString();  // Customize as per your model
//        orderKafkaProducer.sendOrderEvent("order-events", orderData);
//
//        return "Order Created with ID: " + orderId;
//    }
//}

package com.prodify.cqrs.OrderService.command.api.controller;

import com.prodify.cqrs.OrderService.command.api.command.CreateOrderCommand;
import com.prodify.cqrs.OrderService.command.api.kafka.OrderKafkaProducer;
import com.prodify.cqrs.OrderService.command.api.model.OrderRestModel;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderCommandController {

    private final CommandGateway commandGateway;
    private final OrderKafkaProducer orderKafkaProducer;

    @Autowired
    public OrderCommandController(CommandGateway commandGateway, OrderKafkaProducer orderKafkaProducer) {
        this.commandGateway = commandGateway;
        this.orderKafkaProducer = orderKafkaProducer;
    }

    @GetMapping("/message")
    public ResponseEntity<String> getMessage() {
        log.info("Received request for test message");
        return ResponseEntity.ok("Reached orderservice");
    }

    @PostMapping("/order")
    public ResponseEntity<String> createOrder(@RequestBody OrderRestModel orderRestModel) {
        log.info("Received CreateOrder request with details: {}", orderRestModel.getProductId());

        String orderId = UUID.randomUUID().toString();

        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .orderId(orderId)
                .addressId(orderRestModel.getAddressId())
                .userId(orderRestModel.getUserId())
                .productId(orderRestModel.getProductId())
                .quantity(orderRestModel.getQuantity())
                .orderStatus("CREATED")
                .build();

        try {
            // Send the command to the command gateway
            commandGateway.sendAndWait(createOrderCommand);
            log.info("Successfully sent CreateOrderCommand for Order ID: {}", orderId);

            // Prepare data for Kafka and send
            String orderData = orderRestModel.toString();  // Customize to match your model's `toString()` implementation
            orderKafkaProducer.sendOrderEvent("order-events", orderData);
            log.info("Order event successfully published to Kafka for Order ID: {}", orderId);

            return ResponseEntity.ok("Order Created with ID: " + orderId);

        } catch (CommandExecutionException e) {
            log.error("Failed to execute CreateOrderCommand for Order ID: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating order. Please try again later.");

        } catch (Exception e) {
            log.error("Unexpected error while creating order for Order ID: {}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error occurred. Please contact support.");
        }
    }
}

