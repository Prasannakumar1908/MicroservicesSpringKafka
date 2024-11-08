package com.prodify.cqrs.OrderService.command.events;

import com.prodify.cqrs.CommonService.events.OrderCompletedEvent;
import com.prodify.cqrs.OrderService.command.api.data.Order;
import com.prodify.cqrs.OrderService.command.api.data.OrderRepository;
import com.prodify.cqrs.OrderService.command.api.events.OrderEventsHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderEventsHandlerTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderEventsHandler orderEventsHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testOnOrderCompletedEvent() {
        OrderCompletedEvent event = new OrderCompletedEvent("orderId123", "APPROVED");

        Order order = new Order();
        order.setOrderId(event.getOrderId());
        order.setOrderStatus("CREATED");

        when(orderRepository.findById(event.getOrderId())).thenReturn(Optional.of(order));

        orderEventsHandler.on(event);

        verify(orderRepository).save(order);
        assertEquals("APPROVED", order.getOrderStatus());
    }
}
