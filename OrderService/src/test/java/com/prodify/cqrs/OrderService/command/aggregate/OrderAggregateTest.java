package com.prodify.cqrs.OrderService.command.aggregate;

import com.prodify.cqrs.CommonService.commands.CancelOrderCommand;
import com.prodify.cqrs.CommonService.commands.CompleteOrderCommand;
import com.prodify.cqrs.CommonService.events.OrderCancelledEvent;
import com.prodify.cqrs.CommonService.events.OrderCompletedEvent;
import com.prodify.cqrs.OrderService.command.api.aggregate.OrderAggregate;
import com.prodify.cqrs.OrderService.command.api.command.CreateOrderCommand;
import com.prodify.cqrs.OrderService.command.api.command.DeleteOrderCommand;
import com.prodify.cqrs.OrderService.command.api.command.UpdateOrderCommand;
import com.prodify.cqrs.OrderService.command.api.events.OrderCreatedEvent;
import com.prodify.cqrs.OrderService.command.api.events.OrderDeletedEvent;
import com.prodify.cqrs.OrderService.command.api.events.OrderUpdatedEvent;
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
                "productId123",
                "userId123",
                "addressId123",
                2,
                "CREATED",
                "requestId123"
        );

        OrderCreatedEvent expectedEvent = new OrderCreatedEvent(
                "orderId123",
                "productId123",
                "userId123",
                "addressId123",
                2,
                "CREATED",
                "requestId123"
        );

        fixture.givenNoPriorActivity()
                .when(command)
                .expectSuccessfulHandlerExecution()
                .expectEvents(expectedEvent);
    }

    @Test
    void testCompleteOrderCommand() {
        CompleteOrderCommand command = new CompleteOrderCommand("orderId123", "COMPLETED", "requestId123");
        OrderCompletedEvent expectedEvent = new OrderCompletedEvent("orderId123", "COMPLETED", "requestId123");

        fixture.given(new OrderCreatedEvent(
                        "orderId123",
                        "productId123",
                        "userId123",
                        "addressId123",
                        2,
                        "CREATED",
                        "requestId123"))
                .when(command)
                .expectSuccessfulHandlerExecution()
                .expectEvents(expectedEvent);
    }

    @Test
    void testCancelOrderCommand() {
        CancelOrderCommand command = new CancelOrderCommand("orderId123");
        OrderCancelledEvent expectedEvent = new OrderCancelledEvent();
        expectedEvent.setOrderId("orderId123");
        expectedEvent.setOrderStatus("CANCELLED");

        fixture.given(new OrderCreatedEvent(
                        "orderId123",
                        "productId123",
                        "userId123",
                        "addressId123",
                        2,
                        "CREATED",
                        "requestId123"))
                .when(command)
                .expectSuccessfulHandlerExecution()
                .expectEvents(expectedEvent);
    }

    @Test
    void testUpdateOrderCommand() {
        UpdateOrderCommand command = UpdateOrderCommand.builder()
                .orderId("orderId123")
                .productId("updatedProductId")
                .quantity(5)
                .orderStatus("UPDATED")
                .userId("userId123")
                .addressId("updatedAddressId")
                .requestId("requestId123")
                .build();

        OrderUpdatedEvent expectedEvent = new OrderUpdatedEvent(
                "orderId123",
                "updatedProductId",
                5,
                "UPDATED",
                "userId123",
                "updatedAddressId",
                "requestId123"
        );

        fixture.given(new OrderCreatedEvent(
                        "orderId123",
                        "productId123",
                        "userId123",
                        "addressId123",
                        2,
                        "CREATED",
                        "requestId123"))
                .when(command)
                .expectSuccessfulHandlerExecution()
                .expectEvents(expectedEvent);
    }

    @Test
    void testDeleteOrderCommand() {
        DeleteOrderCommand command = new DeleteOrderCommand("orderId123", "requestId123");
        OrderDeletedEvent expectedEvent = new OrderDeletedEvent("orderId123", "requestId123");

        fixture.given(new OrderCreatedEvent(
                        "orderId123",
                        "productId123",
                        "userId123",
                        "addressId123",
                        2,
                        "CREATED",
                        "requestId123"))
                .when(command)
                .expectSuccessfulHandlerExecution()
                .expectEvents(expectedEvent)
                .expectMarkedDeleted();
    }
}
