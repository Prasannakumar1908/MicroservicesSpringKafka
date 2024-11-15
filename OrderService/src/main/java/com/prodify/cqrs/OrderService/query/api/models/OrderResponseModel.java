package com.prodify.cqrs.OrderService.query.api.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponseModel {
    private String orderId;
    private String productId;
    private String userId;
    private String addressId;
    private Integer quantity;
    private String orderStatus;
}
