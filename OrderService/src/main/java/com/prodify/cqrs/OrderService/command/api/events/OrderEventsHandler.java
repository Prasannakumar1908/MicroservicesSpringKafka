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
        log.debug("Received OrderCreatedEvent for Order ID: {} with requestId:{}", event.getOrderId(),event.getRequestId());
        Order order = new Order();
        BeanUtils.copyProperties(event, order);

        try {
            orderRepository.save(order);
            log.info("Order saved in database for Order ID: {} with requestId:{}", order.getOrderId(),event.getRequestId());
        } catch (DataAccessException e) {
            log.error("Error saving order in database for Order ID: {} with requestId:{}", event.getOrderId(),event.getRequestId(), e);
        }catch (Exception e) {
            log.error("Unexpected error while handling OrderCreatedEvent for Order ID: {} with requestId: {}", event.getOrderId(), event.getRequestId(), e);
        }
    }

    @EventHandler
    public void on(OrderCompletedEvent event) {
        log.debug("Received OrderCompletedEvent for Order ID: {} with requestId:{}", event.getOrderId(),event.getRequestId());

        try {
            Optional<Order> orderOptional = orderRepository.findById(event.getOrderId());
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                order.setOrderStatus(event.getOrderStatus());
                orderRepository.save(order);
                log.info("Order status updated to COMPLETED for Order ID: {} with requestId:{}", event.getOrderId(),event.getRequestId());
            } else {
                log.warn("Order not found in database for OrderCompletedEvent. Order ID: {} with requestId:{}", event.getOrderId(),event.getRequestId());
            }
        } catch (DataAccessException e) {
            log.error("Error updating order status to COMPLETED for Order ID: {} with requestId:{}", event.getOrderId(),event.getRequestId(), e);
        }catch (Exception e) {
            log.error("Unexpected error while handling OrderCompletedEvent for Order ID: {} with requestId: {}", event.getOrderId(), event.getRequestId(), e);
        }
    }

    @EventHandler
    public void on(OrderCancelledEvent event) {
        log.debug("Received OrderCancelledEvent for Order ID: {} ", event.getOrderId());

        try {
            Optional<Order> orderOptional = orderRepository.findById(event.getOrderId());
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                order.setOrderStatus(event.getOrderStatus());
                orderRepository.save(order);
                log.info("Order status updated to CANCELLED for Order ID: {} ", event.getOrderId());
            } else {
                log.warn("Order not found in database for OrderCancelledEvent. Order ID: {} ", event.getOrderId());
            }
        } catch (DataAccessException e) {
            log.error("Error updating order status to CANCELLED for Order ID: {} ", event.getOrderId(), e);
        }
    }
    @EventHandler
    public void on(OrderUpdatedEvent event) {
        log.debug("Received OrderUpdatedEvent for Order ID: {} ", event.getOrderId());

        try {
            Optional<Order> orderOptional = orderRepository.findById(event.getOrderId());
            if (orderOptional.isPresent()) {
                Order order = orderOptional.get();
                order.setProductId(event.getProductId());
                order.setQuantity(event.getQuantity());
                order.setOrderStatus(event.getOrderStatus());
                order.setUserId(event.getUserId());
                order.setAddressId(event.getAddressId());
                orderRepository.save(order);
                log.info("Order updated in database for Order ID: {} with requestId:{}", event.getOrderId(), event.getRequestId());
            } else {
                log.warn("Order not found in database for OrderUpdatedEvent. Order ID: {} with requestId:{}", event.getOrderId(), event.getRequestId());
            }
        }catch (DataAccessException e) {
            log.error("Error updating order details for Order ID: {} with requestId: {}", event.getOrderId(), event.getRequestId(), e);
        } catch (Exception e) {
            log.error("Unexpected error while handling OrderUpdatedEvent for Order ID: {} with requestId: {}", event.getOrderId(), event.getRequestId(), e);
        }
    }

    @EventHandler
    public void on(OrderDeletedEvent event) {
        log.debug("Received OrderDeletedEvent for Order ID: {} with requestId:{}", event.getOrderId(),event.getRequestId());

        try {
            orderRepository.deleteById(event.getOrderId());
            log.info("Order deleted from database for Order ID: {} with requestId:{}", event.getOrderId(), event.getRequestId());
        }catch (DataAccessException e) {
            log.error("Error deleting order from database for Order ID: {} with requestId: {}", event.getOrderId(), event.getRequestId(), e);
        } catch (Exception e) {
            log.error("Unexpected error while handling OrderDeletedEvent for Order ID: {} with requestId: {}", event.getOrderId(), event.getRequestId(), e);
        }
    }

}
