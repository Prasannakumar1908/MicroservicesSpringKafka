package com.prodify.cqrs.OrderService.query.api.dto;

import lombok.Data;

@Data
public class SearchRequest {
    private String productId;
    private String userId;
    private int page;
    private int size;
    private String sortBy;
    private String direction;
}
