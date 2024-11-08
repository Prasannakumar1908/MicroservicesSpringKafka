package com.prodify.cqrs.OrderService.command.saga;

import com.prodify.cqrs.CommonService.commands.CompleteOrderCommand;
import com.prodify.cqrs.CommonService.events.OrderCompletedEvent;
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
                "orderId123", "productId456", "userId789", "addressId101", 2, "CREATED"
        );

        User user = new User("userId789", "John Doe", "john@example.com",
                new CardDetails("John Doe", "123456789", 12, 2030, 123));

        // Mock the query to return a valid user as a completed future
        when(queryGateway.query(any(), eq(ResponseTypes.instanceOf(User.class))))
                .thenReturn(CompletableFuture.completedFuture(user));

        fixture.givenNoPriorActivity()
                .whenAggregate(event.getOrderId())
                .publishes(event)
                .expectActiveSagas(1);
//                .expectDispatchedCommands();  // Adjust this if commands should be expected
    }

    @Test
    void testHandleOrderCompletedEvent() {
        OrderCompletedEvent event = new OrderCompletedEvent("orderId123", "APPROVED");

        fixture.givenAggregate(event.getOrderId())
                .published(new OrderCreatedEvent("orderId123", "productId456", "userId789", "addressId101", 2, "CREATED"))
                .whenPublishingA(event)
                .expectActiveSagas(0)  ;// Saga should end after completion
//                .expectDispatchedCommands(new CompleteOrderCommand(event.getOrderId(), event.getOrderStatus()));
    }

}
