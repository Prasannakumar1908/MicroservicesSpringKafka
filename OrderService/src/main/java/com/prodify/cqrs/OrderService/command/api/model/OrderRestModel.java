package com.prodify.cqrs.OrderService.command.api.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRestModel {
    @NotNull(message = "Product ID cannot be null")
    private String productId;

    @NotNull(message = "User Id cannot be null")
    private String userId;

    @NotNull(message = "Address Id cannot be null")
    private String addressId;

    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;
}