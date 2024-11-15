package com.prodify.cqrs.OrderService.command.api.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdatedEvent {
    private String orderId;
    private String productId;
    private Integer quantity;
    private String orderStatus;
    private String userId;
    private String addressId;
}
