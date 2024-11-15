//package com.prodify.cqrs.OrderService.command.api.events;
//
//import com.prodify.cqrs.CommonService.events.OrderCancelledEvent;
//import com.prodify.cqrs.CommonService.events.OrderCompletedEvent;
//import com.prodify.cqrs.OrderService.command.api.data.Order;
//import com.prodify.cqrs.OrderService.command.api.data.OrderRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.axonframework.eventhandling.EventHandler;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
//@Component
//@Slf4j
//public class OrderEventsHandler {
//
//    private OrderRepository orderRepository;
//
//    public OrderEventsHandler(OrderRepository orderRepository) {
//        this.orderRepository = orderRepository;
//    }
//
//    @EventHandler
//    public void on(OrderCreatedEvent event) {
//        log.info("Handling OrderCreatedEvent for Order ID: {}", event.getOrderId());
//        Order order = new Order();
//        BeanUtils.copyProperties(event,order);
//        orderRepository.save(order);
//        log.info("Order saved in database for Order ID: {}", order.getOrderId());
//    }
//
//    @EventHandler
//    public void on(OrderCompletedEvent event) {
//        log.info("Handling OrderCompletedEvent for Order ID: {}", event.getOrderId());
//        Order order
//                = orderRepository.findById(event.getOrderId()).get();
//
//        order.setOrderStatus(event.getOrderStatus());
//
//        orderRepository.save(order);
//        log.info("Order status updated to COMPLETED for Order ID: {}", event.getOrderId());
//    }
//
//    @EventHandler
//    public void on(OrderCancelledEvent event) {
//        log.info("Handling OrderCancelledEvent for Order ID: {}", event.getOrderId());
//        Order order
//                = orderRepository.findById(event.getOrderId()).get();
//
//        order.setOrderStatus(event.getOrderStatus());
//
//        orderRepository.save(order);
//        log.info("Order status updated to CANCELLED for Order ID: {}", event.getOrderId());
//    }
//}

package com.prodify.cqrs.OrderService.command.api.events;

import com.prodify.cqrs.CommonService.events.OrderCancelledEvent;
import com.prodify.cqrs.CommonService.events.OrderCompletedEvent;
import com.prodify.cqrs.OrderService.command.api.data.Order;
import com.prodify.cqrs.OrderService.command.api.data.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class OrderEventsHandler {

    private final OrderRepository orderRepository;

    public OrderEventsHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @EventHandler
    public void on(OrderCreatedEvent event) {
        log.info("Handling OrderCreatedEvent for Order ID: {}", event.getOrderId());
        Order order = new Order();
        BeanUtils.copyProperties(event, order);

        try {
            orderRepository.save(order);
            log.info("Order saved in database for Order ID: {}", order.getOrderId());
        } catch (DataAccessException e) {
            log.error("Error saving order in database for Order ID: {}", event.getOrderId(), e);
        }
    }

    @EventHandler
    public void on(OrderCompletedEvent event) {
        log.info("Handling OrderCompletedEvent for Order ID: {}", event.getOrderId());

        try {
            Optional<Order> orderOptional = orderRepository.findById(event.getOrderId());
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                order.setOrderStatus(event.getOrderStatus());
                orderRepository.save(order);
                log.info("Order status updated to COMPLETED for Order ID: {}", event.getOrderId());
            } else {
                log.warn("Order not found in database for OrderCompletedEvent. Order ID: {}", event.getOrderId());
            }
        } catch (DataAccessException e) {
            log.error("Error updating order status to COMPLETED for Order ID: {}", event.getOrderId(), e);
        }
    }

    @EventHandler
    public void on(OrderCancelledEvent event) {
        log.info("Handling OrderCancelledEvent for Order ID: {}", event.getOrderId());

        try {
            Optional<Order> orderOptional = orderRepository.findById(event.getOrderId());
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                order.setOrderStatus(event.getOrderStatus());
                orderRepository.save(order);
                log.info("Order status updated to CANCELLED for Order ID: {}", event.getOrderId());
            } else {
                log.warn("Order not found in database for OrderCancelledEvent. Order ID: {}", event.getOrderId());
            }
        } catch (DataAccessException e) {
            log.error("Error updating order status to CANCELLED for Order ID: {}", event.getOrderId(), e);
        }
    }
    @EventHandler
    public void on(OrderUpdatedEvent event) {
        log.info("Handling OrderUpdatedEvent for Order ID: {}", event.getOrderId());

        Optional<Order> orderOptional = orderRepository.findById(event.getOrderId());
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setProductId(event.getProductId());
            order.setQuantity(event.getQuantity());
            order.setOrderStatus(event.getOrderStatus());
            order.setUserId(event.getUserId());
            order.setAddressId(event.getAddressId());
            orderRepository.save(order);
            log.info("Order updated in database for Order ID: {}", event.getOrderId());
        } else {
            log.warn("Order not found in database for OrderUpdatedEvent. Order ID: {}", event.getOrderId());
        }
    }

    @EventHandler
    public void on(OrderDeletedEvent event) {
        log.info("Handling OrderDeletedEvent for Order ID: {}", event.getOrderId());

        orderRepository.deleteById(event.getOrderId());
        log.info("Order deleted from database for Order ID: {}", event.getOrderId());
    }

}
