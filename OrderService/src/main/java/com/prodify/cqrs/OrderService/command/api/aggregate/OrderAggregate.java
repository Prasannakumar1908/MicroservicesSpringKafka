package com.prodify.cqrs.OrderService.command.api.aggregate;

import com.prodify.cqrs.CommonService.commands.CancelOrderCommand;
import com.prodify.cqrs.CommonService.commands.CompleteOrderCommand;
import com.prodify.cqrs.CommonService.events.OrderCancelledEvent;
import com.prodify.cqrs.CommonService.events.OrderCompletedEvent;
import com.prodify.cqrs.OrderService.command.api.command.CreateOrderCommand;
import com.prodify.cqrs.OrderService.command.api.command.DeleteOrderCommand;
import com.prodify.cqrs.OrderService.command.api.command.UpdateOrderCommand;
import com.prodify.cqrs.OrderService.command.api.events.OrderCreatedEvent;
import com.prodify.cqrs.OrderService.command.api.events.OrderDeletedEvent;
import com.prodify.cqrs.OrderService.command.api.events.OrderUpdatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

@Aggregate
@Slf4j
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;
    private String productId;
    private String userId;
    private String addressId;
    private Integer quantity;
    private String orderStatus;
    private String requestId;

    public OrderAggregate() {
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {
        try {
            log.debug("Received CreateOrderCommand for Order Id:{} with request Id:{}",createOrderCommand.getOrderId(),createOrderCommand.getRequestId());
            validateCreateOrderCommand(createOrderCommand);
            log.info("Validating CreateOrderCommand for Order Id:{} with requestId:{} ", createOrderCommand.getOrderId(),createOrderCommand.getRequestId());
            OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                    createOrderCommand.getOrderId(),
                    createOrderCommand.getProductId(),
                    createOrderCommand.getUserId(),
                    createOrderCommand.getAddressId(),
                    createOrderCommand.getQuantity(),
                    createOrderCommand.getOrderStatus(),
                    createOrderCommand.getRequestId()
            );
            BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);
            AggregateLifecycle.apply(orderCreatedEvent);
            log.info("Applied OrderCreatedEvent for Order Id:{} with requestId:{}", orderCreatedEvent.getOrderId(),createOrderCommand.getRequestId());
        } catch (IllegalArgumentException e) {
            log.error("CreateOrderCommand validation failed: {} with requestId:{}", e.getMessage(),createOrderCommand.getRequestId());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while handling CreateOrderCommand: {} with requestId:{}", e.getMessage(),createOrderCommand.getRequestId());
            throw new RuntimeException("Failed to create order", e);
        }
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        log.debug("Event sourcing OrderCreatedEvent for Order Id:{} with requestId:{}", event.getOrderId(),event.getRequestId());
        this.orderStatus = event.getOrderStatus();
        this.userId = event.getUserId();
        this.orderId = event.getOrderId();
        this.quantity = event.getQuantity();
        this.productId = event.getProductId();
        this.addressId = event.getAddressId();
        this.requestId = event.getRequestId();

    }

    @CommandHandler
    public void handle(CompleteOrderCommand completeOrderCommand) {
        try {
            log.debug("Received CompleteOrderCommand for OrderId:{} with requestId:{}",completeOrderCommand.getOrderId(),completeOrderCommand.getRequestId());
            validateCompleteOrderCommand(completeOrderCommand);
            log.info("Validating CompleteOrderCommand for Order Id:{} with requestId:{}", completeOrderCommand.getOrderId(),completeOrderCommand.getRequestId());
            OrderCompletedEvent orderCompletedEvent = OrderCompletedEvent.builder()
                    .orderStatus(completeOrderCommand.getOrderStatus())
                    .orderId(completeOrderCommand.getOrderId())
                    .requestId(completeOrderCommand.getRequestId())
                    .build();
            AggregateLifecycle.apply(orderCompletedEvent);
            log.info("Applied OrderCompletedEvent for OrderId:{} with requestId:{}", orderCompletedEvent.getOrderId(),orderCompletedEvent.getRequestId());
        } catch (IllegalArgumentException e) {
            log.error("CompleteOrderCommand validation failed: {} with requestId:{}", e.getMessage(),completeOrderCommand.getRequestId());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while handling CompleteOrderCommand: {} with requestId:{}", e.getMessage(),completeOrderCommand.getRequestId());
            throw new RuntimeException("Failed to complete order", e);
        }
    }

    @EventSourcingHandler
    public void on(OrderCompletedEvent event) {
        log.debug("Event sourcing OrderCompletedEvent for Order Id:{} with requestId:{}", event.getOrderId(),event.getRequestId());
        this.orderStatus = event.getOrderStatus();
        this.requestId = event.getRequestId();
    }

    @CommandHandler
    public void handle(CancelOrderCommand cancelOrderCommand) {
        try {
            log.debug("Received CancelOrdercommand for OrderId:{}",cancelOrderCommand.getOrderId());
            validateCancelOrderCommand(cancelOrderCommand);
            log.info("Handling CancelOrderCommand for Order Id:{}", cancelOrderCommand.getOrderId());
            OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent();
            BeanUtils.copyProperties(cancelOrderCommand, orderCancelledEvent);
            AggregateLifecycle.apply(orderCancelledEvent);
            log.info("Applied OrderCancelledEvent for OrderId:{]",cancelOrderCommand.getOrderId());
        } catch (IllegalArgumentException e) {
            log.error("CancelOrderCommand validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while handling CancelOrderCommand: {}", e.getMessage());
            throw new RuntimeException("Failed to cancel order", e);
        }
    }

    @EventSourcingHandler
    public void on(OrderCancelledEvent event) {
        log.debug("Event sourcing OrderCancelledEvent for Order Id:{}", event.getOrderId());
        this.orderStatus = event.getOrderStatus();
    }


    @CommandHandler
    public void handle(UpdateOrderCommand updateOrderCommand) {
        try {
            log.debug("Received UpdateOrderCommand for Order Id:{} with requestId:{}", updateOrderCommand.getOrderId(), updateOrderCommand.getRequestId());
            log.info("Handling UpdateOrderCommand for Order Id:{} with requestId:{}", updateOrderCommand.getOrderId(), updateOrderCommand.getRequestId());


            OrderUpdatedEvent orderUpdatedEvent = new OrderUpdatedEvent(
                    updateOrderCommand.getOrderId(),
                    updateOrderCommand.getProductId(),
                    updateOrderCommand.getQuantity(),
                    updateOrderCommand.getOrderStatus(),
                    updateOrderCommand.getUserId(),
                    updateOrderCommand.getAddressId(),
                    updateOrderCommand.getRequestId()
            );

            AggregateLifecycle.apply(orderUpdatedEvent);
            log.info("Applied OrderUpdatedEvent for OrderId:{} with requestId:{}", updateOrderCommand.getOrderId(), updateOrderCommand.getRequestId());
        } catch (Exception e) {
            log.error("Unexpected error occurred while handling UpdateOrderCommand: {}", e.getMessage());
            throw new RuntimeException("Failed to update order",e);
        }

    }

    @EventSourcingHandler
    public void on(OrderUpdatedEvent event) {
        log.debug("Event sourcing OrderUpdatedEvent for Order Id:{} with requestId:{}", event.getOrderId(),event.getRequestId());
        this.productId = event.getProductId();
        this.quantity = event.getQuantity();
        this.orderStatus = event.getOrderStatus();
        this.userId = event.getUserId();
        this.addressId = event.getAddressId();
    }

    @CommandHandler
    public void handle(DeleteOrderCommand deleteOrderCommand) {
        try {
            log.debug("Handling DeleteOrderCommand for Order Id:{} with requestId:{}", deleteOrderCommand.getOrderId(), deleteOrderCommand.getRequestId());

            OrderDeletedEvent orderDeletedEvent = new OrderDeletedEvent(deleteOrderCommand.getOrderId(), deleteOrderCommand.getRequestId());
            AggregateLifecycle.apply(orderDeletedEvent);
            log.info("Applied OrderDeletedEvent for Order Id:{} with requestId:{}",deleteOrderCommand.getOrderId(), deleteOrderCommand.getRequestId());
        } catch (Exception e) {
            log.error("Unexpected error occurred while handling DeleteOrderCommand: {}", e.getMessage());
            throw new RuntimeException("Failed to delete order",e);
        }

    }

    @EventSourcingHandler
    public void on(OrderDeletedEvent event) {
        log.debug("Event sourcing OrderDeletedEvent for Order Id:{} with requestId:{}", event.getOrderId(),event.getRequestId());
        AggregateLifecycle.markDeleted();
    }

    private void validateCreateOrderCommand(CreateOrderCommand command) {
        log.debug("Validating CreateorderCommand for Order Id:{}",command.getOrderId());
        if (command == null || !StringUtils.hasText(command.getOrderId())) {
            log.warn("Order ID is missing in CreateOrderCommand");
            throw new IllegalArgumentException("Order ID cannot be null or empty in CreateOrderCommand");
        }
        if (!StringUtils.hasText(command.getProductId()) || !StringUtils.hasText(command.getUserId())) {
            log.warn("Product ID or User ID is missing in CreateOrderCommand");
            throw new IllegalArgumentException("Product ID and User ID cannot be null or empty in CreateOrderCommand");
        }
        if (command.getQuantity() == null || command.getQuantity() <= 0) {
            log.warn("Invalid quantity in CreateOrderCommand");
            throw new IllegalArgumentException("Quantity must be greater than zero in CreateOrderCommand");
        }
    }

    private void validateCompleteOrderCommand(CompleteOrderCommand command) {
        log.debug("Validating CompleteOrderCommand for Order Id:{}",command.getOrderId());
        if (command == null || !StringUtils.hasText(command.getOrderId())) {
            log.warn("Order ID is missing in CompleteOrderCommand");
            throw new IllegalArgumentException("Order ID cannot be null or empty in CompleteOrderCommand");
        }
        if (!StringUtils.hasText(command.getOrderStatus())) {
            log.warn("Order status is missing in CompleteOrderCommand");
            throw new IllegalArgumentException("Order Status cannot be null or empty in CompleteOrderCommand");
        }
    }

    private void validateCancelOrderCommand(CancelOrderCommand command) {
        log.debug("Validating CancelOrderCommand for Order Id:{}",command.getOrderId());
        if (command == null || !StringUtils.hasText(command.getOrderId())) {
            log.warn("Order ID is missing in CancelOrderCommand");
            throw new IllegalArgumentException("Order ID cannot be null or empty in CancelOrderCommand");
        }
    }

}
