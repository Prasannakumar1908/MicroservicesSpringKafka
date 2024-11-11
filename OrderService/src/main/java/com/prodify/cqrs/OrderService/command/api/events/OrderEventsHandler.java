package com.prodify.cqrs.OrderService.command.api.events;

import com.prodify.cqrs.CommonService.events.OrderCancelledEvent;
import com.prodify.cqrs.CommonService.events.OrderCompletedEvent;
import com.prodify.cqrs.OrderService.command.api.data.Order;
import com.prodify.cqrs.OrderService.command.api.data.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventsHandler {

    private OrderRepository orderRepository;

    public OrderEventsHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        log.info("Handling OrderCreatedEvent for Order ID: {}", event.getOrderId());
        Order order = new Order();
        BeanUtils.copyProperties(event,order);
        orderRepository.save(order);
        log.info("Order saved in database for Order ID: {}", order.getOrderId());
    }

    @EventHandler
    public void on(OrderCompletedEvent event) {
        log.info("Handling OrderCompletedEvent for Order ID: {}", event.getOrderId());
        Order order
                = orderRepository.findById(event.getOrderId()).get();

        order.setOrderStatus(event.getOrderStatus());

        orderRepository.save(order);
        log.info("Order status updated to COMPLETED for Order ID: {}", event.getOrderId());
    }

    @EventHandler
    public void on(OrderCancelledEvent event) {
        log.info("Handling OrderCancelledEvent for Order ID: {}", event.getOrderId());
        Order order
                = orderRepository.findById(event.getOrderId()).get();

        order.setOrderStatus(event.getOrderStatus());

        orderRepository.save(order);
        log.info("Order status updated to CANCELLED for Order ID: {}", event.getOrderId());
    }
}
