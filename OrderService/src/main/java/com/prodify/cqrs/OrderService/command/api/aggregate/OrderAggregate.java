package com.prodify.cqrs.OrderService.command.api.aggregate;

import com.prodify.cqrs.CommonService.commands.CancelOrderCommand;
import com.prodify.cqrs.CommonService.commands.CompleteOrderCommand;
import com.prodify.cqrs.CommonService.events.OrderCancelledEvent;
import com.prodify.cqrs.CommonService.events.OrderCompletedEvent;
import com.prodify.cqrs.OrderService.command.api.command.CreateOrderCommand;
import com.prodify.cqrs.OrderService.command.api.events.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

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

    public OrderAggregate() {
    }

    @CommandHandler
    public OrderAggregate(CreateOrderCommand createOrderCommand) {
        //Validate The Command
        log.info("Handling CreateOrderCommand for Order Id:{}", createOrderCommand.getOrderId());
        OrderCreatedEvent orderCreatedEvent
                = new OrderCreatedEvent(
                        createOrderCommand.getOrderId(),
                        createOrderCommand.getProductId(),
                        createOrderCommand.getUserId(),
                        createOrderCommand.getAddressId(),
                        createOrderCommand.getQuantity(),
                        createOrderCommand.getOrderStatus()
        );
        BeanUtils.copyProperties(createOrderCommand,
                orderCreatedEvent);
        AggregateLifecycle.apply(orderCreatedEvent);
        log.info("Applied OrderCreatedEvent for Order Id:{}", orderCreatedEvent.getOrderId());
    }

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        log.info("Event sourcing OrderCreatedEvent for Order Id:{}", event.getOrderId());
        this.orderStatus = event.getOrderStatus();
        this.userId = event.getUserId();
        this.orderId = event.getOrderId();
        this.quantity = event.getQuantity();
        this.productId = event.getProductId();
        this.addressId = event.getAddressId();
    }

    @CommandHandler
    public void handle(CompleteOrderCommand completeOrderCommand) {
        //Validate The Command
        // Publish Order Completed Event
        log.info("Handling CompleteOrderCommand for Order Id:{}", completeOrderCommand.getOrderId());
        OrderCompletedEvent orderCompletedEvent
                = OrderCompletedEvent.builder()
                .orderStatus(completeOrderCommand.getOrderStatus())
                .orderId(completeOrderCommand.getOrderId())
                .build();
        AggregateLifecycle.apply(orderCompletedEvent);
    }

    @EventSourcingHandler
    public void on(OrderCompletedEvent event) {
        log.info("Event sourcing OrderCompletedEvent for Order Id:{}", event.getOrderId());
        this.orderStatus = event.getOrderStatus();
    }

    @CommandHandler
    public void handle(CancelOrderCommand cancelOrderCommand) {
        log.info("Handling CancelOrderCommand for Order Id:{}", cancelOrderCommand.getOrderId());
        OrderCancelledEvent orderCancelledEvent
                = new OrderCancelledEvent();
        BeanUtils.copyProperties(cancelOrderCommand,
                orderCancelledEvent);

        AggregateLifecycle.apply(orderCancelledEvent);
    }

    @EventSourcingHandler
    public void on(OrderCancelledEvent event) {
        log.info("Event sourcing OrderCancelledEvent for Order Id:{}", event.getOrderId());
        this.orderStatus = event.getOrderStatus();
    }
}
