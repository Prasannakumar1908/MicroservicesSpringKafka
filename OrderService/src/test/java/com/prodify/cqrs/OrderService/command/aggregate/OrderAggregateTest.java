package com.prodify.cqrs.OrderService.command.aggregate;

import com.prodify.cqrs.CommonService.commands.CompleteOrderCommand;
import com.prodify.cqrs.CommonService.events.OrderCompletedEvent;
import com.prodify.cqrs.OrderService.command.api.aggregate.OrderAggregate;
import com.prodify.cqrs.OrderService.command.api.command.CreateOrderCommand;
import com.prodify.cqrs.OrderService.command.api.events.OrderCreatedEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderAggregateTest {

    private AggregateTestFixture<OrderAggregate> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(OrderAggregate.class);
    }

    @Test
    void testCreateOrderCommand() {
        CreateOrderCommand command = new CreateOrderCommand(
                "orderId123",
                "productId456",
                "userId789",
                "addressId101",
                2,
                "CREATED"
        );

        OrderCreatedEvent event = new OrderCreatedEvent(
                command.getOrderId(),
                command.getProductId(),
                command.getUserId(),
                command.getAddressId(),
                command.getQuantity(),
                command.getOrderStatus()
        );

        fixture.givenNoPriorActivity()
                .when(command)
                .expectSuccessfulHandlerExecution()
                .expectEvents(event);
    }

    @Test
    void testCompleteOrderCommand() {
        CompleteOrderCommand command = new CompleteOrderCommand("orderId123", "APPROVED");

        OrderCompletedEvent event = new OrderCompletedEvent(
                command.getOrderId(),
                command.getOrderStatus()
        );

        fixture.givenCommands(new CreateOrderCommand("orderId123", "productId456", "userId789", "addressId101", 2, "CREATED"))
                .when(command)
                .expectSuccessfulHandlerExecution()
                .expectEvents(event);
    }
}
