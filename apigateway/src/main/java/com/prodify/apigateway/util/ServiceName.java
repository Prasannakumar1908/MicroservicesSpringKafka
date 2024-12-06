package com.prodify.apigateway.util;

public enum ServiceName {
    PRODUCT_SERVICE_URL("lb://product-service/products/"),
    ORDER_SERVICE_URL("lb://order-service/orders/"),
    USER_SERVICE_URL("lb://user-service/users/");

    public final String serviceUrl;

    private ServiceName(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
}
