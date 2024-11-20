package com.prodify.cqrs.OrderService.command.events;

import com.prodify.cqrs.CommonService.events.OrderCancelledEvent;
import com.prodify.cqrs.CommonService.events.OrderCompletedEvent;
import com.prodify.cqrs.OrderService.command.api.data.Order;
import com.prodify.cqrs.OrderService.command.api.data.OrderRepository;
import com.prodify.cqrs.OrderService.command.api.events.OrderCreatedEvent;
import com.prodify.cqrs.OrderService.command.api.events.OrderDeletedEvent;
import com.prodify.cqrs.OrderService.command.api.events.OrderEventsHandler;
import com.prodify.cqrs.OrderService.command.api.events.OrderUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderEventsHandlerTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderEventsHandler orderEventsHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderEventsHandler = new OrderEventsHandler(orderRepository);
    }

    @Test
    void testHandleOrderCreatedEvent() {
        // Arrange
        OrderCreatedEvent event = new OrderCreatedEvent("orderId123", "productId123", "userId123", "addressId123", 2, "CREATED", "requestId123");
        Order order = new Order();
        order.setOrderId(event.getOrderId());

        // Act
        orderEventsHandler.on(event);

        // Assert
        verify(orderRepository, times(1)).save(any(Order.class));
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(event.getOrderId(), orderCaptor.getValue().getOrderId());
        assertEquals(event.getOrderStatus(), orderCaptor.getValue().getOrderStatus());
    }

    @Test
    void testHandleOrderCompletedEvent() {
        // Arrange
        Order order = new Order();
        order.setOrderId("orderId123");
        order.setOrderStatus("CREATED");

        when(orderRepository.findById("orderId123")).thenReturn(Optional.of(order));

        OrderCompletedEvent event = new OrderCompletedEvent("orderId123", "COMPLETED", "requestId123");

        // Act
        orderEventsHandler.on(event);

        // Assert
        verify(orderRepository, times(1)).save(any(Order.class));
        assertEquals("COMPLETED", order.getOrderStatus());
    }

    @Test
    void testHandleOrderCancelledEvent() {
        // Arrange
        Order order = new Order();
        order.setOrderId("orderId123");
        order.setOrderStatus("CREATED");

        when(orderRepository.findById("orderId123")).thenReturn(Optional.of(order));

        OrderCancelledEvent event = new OrderCancelledEvent("orderId123", "CANCELLED");

        // Act
        orderEventsHandler.on(event);

        // Assert
        verify(orderRepository, times(1)).save(any(Order.class));
        assertEquals("CANCELLED", order.getOrderStatus());
    }

    @Test
    void testHandleOrderUpdatedEvent() {
        // Arrange
        Order order = new Order();
        order.setOrderId("orderId123");

        when(orderRepository.findById("orderId123")).thenReturn(Optional.of(order));

        OrderUpdatedEvent event = new OrderUpdatedEvent("orderId123", "productId456", 3, "UPDATED", "userId456", "addressId456", "requestId123");

        // Act
        orderEventsHandler.on(event);

        // Assert
        verify(orderRepository, times(1)).save(any(Order.class));
        assertEquals(event.getProductId(), order.getProductId());
        assertEquals(event.getQuantity(), order.getQuantity());
        assertEquals(event.getOrderStatus(), order.getOrderStatus());
    }

    @Test
    void testHandleOrderDeletedEvent() {
        // Arrange
        OrderDeletedEvent event = new OrderDeletedEvent("orderId123", "requestId123");

        // Act
        orderEventsHandler.on(event);

        // Assert
        verify(orderRepository, times(1)).deleteById("orderId123");
    }
}
