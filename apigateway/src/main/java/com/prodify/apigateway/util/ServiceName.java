//package com.prodify.apigateway.util;
//
//public enum ServiceName {
//    ORDER_SERVICE("order-service/orders/", "order"),
//    PRODUCT_SERVICE("product-service", "/products"),
//    USER_SERVICE("user-service", "/users");
//
//    public final String serviceName;
//    public final String path;
//
//    ServiceName(String serviceName, String path) {
//        this.serviceName = serviceName;
//        this.path = path;
//    }
//
//    // Use service name for load balancing and base path
//    public String getServiceUri() {
//        return "lb://" + this.serviceName + this.path;
//    }
//}

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
