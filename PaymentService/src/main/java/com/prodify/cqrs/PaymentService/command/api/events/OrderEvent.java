package com.prodify.cqrs.PaymentService.command.api.events;

public class OrderEvent {
    private String orderId;
    private String addressId;
    private String userId;
    private String productId;
    private int quantity;
    private String orderStatus;
    private String requestId;

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getAddressId() { return addressId; }
    public void setAddressId(String addressId) { this.addressId = addressId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
}
