package com.prodify.cqrs.OrderService.query.api.model;

import lombok.Data;

@Data
public class OrderRestModel {
    private String orderId;
    private String productId;
    private String userId;
    private String addressId;
    private Integer quantity;
    private String orderStatus;
    private String requestId;
}
