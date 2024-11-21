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
    private int page;     // Page number (0-based)
    private int size;     // Page size
    private String sortBy; // Field to sort by (e.g., "quantity", "productId")
    private String direction; // Sorting direction: "asc" or "desc"
}
