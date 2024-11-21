package com.prodify.cqrs.OrderService.query.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest {
    private String productId;
    private String userId;
    private String addressId;
    private Integer quantityMin;
    private Integer quantityMax;
}
