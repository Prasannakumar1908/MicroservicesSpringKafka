package com.prodify.cqrs.OrderService.command.api.controller;

import com.prodify.cqrs.OrderService.command.api.command.CreateOrderCommand;
import com.prodify.cqrs.OrderService.command.api.kafka.OrderKafkaProducer;
import com.prodify.cqrs.OrderService.command.api.model.OrderRestModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@Tag(name = "Order Command Controller", description = "Operations related to Order")

public class OrderCommandController {

    private CommandGateway commandGateway;

    private final OrderKafkaProducer orderKafkaProducer;


    @Autowired
    public OrderCommandController(CommandGateway commandGateway, OrderKafkaProducer orderKafkaProducer) {
        this.commandGateway = commandGateway;
        this.orderKafkaProducer = orderKafkaProducer;
    }

    @GetMapping("/message")
    @Operation(summary = "Test Order Service", description = "Returns a test message from Order Service")

    public String getMessage(){
        return "Reached orderservice";
    }

    @PostMapping("/order")
    @Operation(summary = "Create an Order", description = "Creates a new order with the provided details")

    public String createOrder(@RequestBody OrderRestModel orderRestModel) {

        String orderId = UUID.randomUUID().toString();

        CreateOrderCommand createOrderCommand
                = CreateOrderCommand.builder()
                .orderId(orderId)
                .addressId(orderRestModel.getAddressId())
                .productId(orderRestModel.getProductId())
                .quantity(orderRestModel.getQuantity())
                .orderStatus("CREATED")
                .build();

        commandGateway.sendAndWait(createOrderCommand);
        String orderData = orderRestModel.toString();  // Customize as per your model
        orderKafkaProducer.sendOrderEvent("order-events", orderData);

        return "Order Created with ID: " + orderId;
    }
}
