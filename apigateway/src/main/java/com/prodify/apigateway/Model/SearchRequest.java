package com.prodify.apigateway.Model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SearchRequest {
    private String productId;
    private String userId;
    private String addressId;
    private Integer quantityMin;
    private Integer quantityMax;
}
