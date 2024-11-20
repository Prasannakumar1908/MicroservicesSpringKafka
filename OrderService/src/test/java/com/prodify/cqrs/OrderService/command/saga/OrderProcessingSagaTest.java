package com.prodify.cqrs.OrderService.command.saga;

import com.prodify.cqrs.CommonService.commands.*;
import com.prodify.cqrs.CommonService.events.*;
import com.prodify.cqrs.OrderService.command.api.events.OrderCreatedEvent;
import com.prodify.cqrs.OrderService.command.api.saga.OrderProcessingSaga;
import com.prodify.cqrs.CommonService.model.CardDetails;
import com.prodify.cqrs.CommonService.model.User;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.test.saga.SagaTestFixture;
import org.axonframework.test.matchers.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

public class OrderProcessingSagaTest {

    private SagaTestFixture<OrderProcessingSaga> fixture;

    @Mock
    private CommandGateway commandGateway;

    @Mock
    private QueryGateway queryGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fixture = new SagaTestFixture<>(OrderProcessingSaga.class);
        fixture.registerCommandGateway(CommandGateway.class, commandGateway);

        // Directly inject the QueryGateway mock into the fixture's resource injector
        fixture.registerResource(queryGateway);
    }

    @Test
    void testHandleOrderCreatedEvent() {
        OrderCreatedEvent event = new OrderCreatedEvent(
                "orderId123", "productId456", "userId789", "addressId101", 2, "CREATED", "abc"
        );

        User user = new User("userId789", "John Doe", "john@example.com",
                new CardDetails("John Doe", "123456789", 12, 2030, 123));

        // Mock the query to return a valid user as a completed future
        when(queryGateway.query(any(), eq(ResponseTypes.instanceOf(User.class))))
                .thenReturn(CompletableFuture.completedFuture(user));

        fixture.givenNoPriorActivity()
                .whenAggregate(event.getOrderId())
                .publishes(event)
                .expectActiveSagas(1)
                // Make sure the dispatched command matches the expected one.
                .expectDispatchedCommands(ValidatePaymentCommand.class);  // Expect ValidatePaymentCommand to be sent
    }


    @Test
    void testHandleOrderCompletedEvent() {
        OrderCompletedEvent event = new OrderCompletedEvent("orderId123", "APPROVED","abc");

        fixture.givenAggregate(event.getOrderId())
                .published(new OrderCreatedEvent("orderId123", "productId456", "userId789", "addressId101", 2, "CREATED","abc"))
                .whenPublishingA(event)
                .expectActiveSagas(0)  // Saga should end after completion
                .expectDispatchedCommands(CompleteOrderCommand.class);  // Expect CompleteOrderCommand to be sent
    }

    @Test
    void testHandlePaymentProcessedEvent() {
        PaymentProcessedEvent event = new PaymentProcessedEvent("orderId123", "paymentId456");

        fixture.givenAggregate("orderId123")
                .published(new OrderCreatedEvent("orderId123", "productId456", "userId789", "addressId101", 2, "CREATED","abc"))
                .whenPublishingA(event)
                .expectActiveSagas(1)  // Saga should still be active
                .expectDispatchedCommands(ShipOrderCommand.class);  // Expect ShipOrderCommand to be sent
    }

    @Test
    void testHandleOrderShippedEvent() {
        OrderShippedEvent event = new OrderShippedEvent("shipmentId789", "orderId123", "SHIPPED");

        fixture.givenAggregate("orderId123")
                .published(new OrderCreatedEvent("orderId123", "productId456", "userId789", "addressId101", 2, "CREATED","abc"))
                .whenPublishingA(event)
                .expectActiveSagas(0)  // Saga should end after completion
                .expectDispatchedCommands(CompleteOrderCommand.class);  // Expect CompleteOrderCommand to be sent
    }

    @Test
    void testHandlePaymentCancelledEvent() {
        PaymentCancelledEvent event = new PaymentCancelledEvent("orderId123", "paymentId456","CANCELLED");

        fixture.givenAggregate("orderId123")
                .published(new OrderCreatedEvent("orderId123", "productId456", "userId789", "addressId101", 2, "CANCELLED","abc"))
                .whenPublishingA(event)
                .expectActiveSagas(1)  // Saga should still be active
                .expectDispatchedCommands(CancelOrderCommand.class);  // Expect CancelOrderCommand to be sent
    }

    @Test
    void testHandleOrderCancelledEvent() {
        OrderCancelledEvent event = new OrderCancelledEvent("orderId123","CANCELLED");

        fixture.givenAggregate("orderId123")
                .published(new OrderCreatedEvent("orderId123", "productId456", "userId789", "addressId101", 2, "CANCELLED","abc"))
                .whenPublishingA(event)
                .expectActiveSagas(0)  // Saga should end after completion
                .expectDispatchedCommands();  // No command is expected after cancellation
    }
}
