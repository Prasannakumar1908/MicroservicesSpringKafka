package com.prodify.apigateway.Model;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrderRestModel {

//#randomid_productId_productId
    @NotNull(message = "Product ID cannot be null")
    private String productId;

    @NotNull(message = "User Id cannot be null")
    private String userId;

    @NotNull(message = "Address Id cannot be null")
    private String addressId;

    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
