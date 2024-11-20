package com.prodify.cqrs.OrderService.command.saga;

import com.prodify.cqrs.CommonService.commands.*;
import com.prodify.cqrs.CommonService.events.*;
import com.prodify.cqrs.OrderService.command.api.events.OrderCreatedEvent;
import com.prodify.cqrs.OrderService.command.api.saga.OrderProcessingSaga;
import com.prodify.cqrs.CommonService.model.CardDetails;
import com.prodify.cqrs.CommonService.model.User;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class OrderProcessingSagaTest {

    private SagaTestFixture<OrderProcessingSaga> fixture;

    @Mock
    private transient CommandGateway commandGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fixture = new SagaTestFixture<>(OrderProcessingSaga.class);
        fixture.registerCommandGateway(CommandGateway.class, commandGateway);
    }

//    @Test
//    void testHandleOrderCreatedEvent() {
//        // Given an OrderCreatedEvent
//        OrderCreatedEvent event = new OrderCreatedEvent("orderId123", "productId456", "userId789", "addressId101", 2, "CREATED", "abc");
//
//        // When
//        fixture.givenNoPriorActivity()
//                .whenAggregate(event.getOrderId())
//                .publishes(event)
//                .expectActiveSagas(1)
//                .expectDispatchedCommands(ValidatePaymentCommand.class);  // Expect ValidatePaymentCommand to be sent
//    }
//
//    @Test
//    void testHandleOrderCompletedEvent() {
//        // Given an OrderCompletedEvent after an order is created
//        OrderCompletedEvent event = new OrderCompletedEvent("orderId123", "APPROVED", "abc");
//
//        fixture.givenAggregate("orderId123")
//                .published(new OrderCreatedEvent("orderId123", "productId456", "userId789", "addressId101", 2, "CREATED", "abc"))
//                .whenPublishingA(event)
//                .expectActiveSagas(0)  // Saga should end after completion
//                .expectDispatchedCommands(CompleteOrderCommand.class);  // Expect CompleteOrderCommand to be sent
//    }
//
//    @Test
//    void testHandlePaymentProcessedEvent() {
//        // Given a PaymentProcessedEvent after order creation
//        PaymentProcessedEvent event = new PaymentProcessedEvent("orderId123", "paymentId456");
//
//        fixture.givenAggregate("orderId123")
//                .published(new OrderCreatedEvent("orderId123", "productId456", "userId789", "addressId101", 2, "CREATED", "abc"))
//                .whenPublishingA(event)
//                .expectActiveSagas(1)  // Saga should still be active
//                .expectDispatchedCommands(ShipOrderCommand.class);  // Expect ShipOrderCommand to be sent
//    }
//
//
//
//    @Test
//    void testHandlePaymentCancelledEvent() {
//        // Given a PaymentCancelledEvent after order creation
//        PaymentCancelledEvent event = new PaymentCancelledEvent("orderId123", "paymentId456", "CANCELLED");
//
//        fixture.givenAggregate("orderId123")
//                .published(new OrderCreatedEvent("orderId123", "productId456", "userId789", "addressId101", 2, "CREATED", "abc"))
//                .whenPublishingA(event)
//                .expectActiveSagas(1)  // Saga should still be active
//                .expectDispatchedCommands(CancelOrderCommand.class);  // Expect CancelOrderCommand to be sent
//    }

    @Test
    void testHandleOrderCancelledEvent() {
        // Given an OrderCancelledEvent after the order was created
        OrderCancelledEvent event = new OrderCancelledEvent("orderId123", "CANCELLED");

        fixture.givenAggregate("orderId123")
                .published(new OrderCreatedEvent("orderId123", "productId456", "userId789", "addressId101", 2, "CREATED", "abc"))
                .whenPublishingA(event)
                .expectActiveSagas(0)  // Saga should end after cancellation
                .expectDispatchedCommands();  // No command is expected after cancellation
    }
}
