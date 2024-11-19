package com.prodify.cqrs.OrderService.command.api.command;


import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@Builder
public class UpdateOrderCommand {
    @TargetAggregateIdentifier
    private String orderId;
    private String productId;
    private Integer quantity;
    private String orderStatus;
    private String userId;
    private String addressId;
    private String requestId;
}

