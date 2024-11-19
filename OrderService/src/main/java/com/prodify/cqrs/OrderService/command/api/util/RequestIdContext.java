package com.prodify.cqrs.OrderService.command.api.util;

import org.springframework.stereotype.Component;

@Component
public class RequestIdContext {

    private static final ThreadLocal<String> requestId = new ThreadLocal<>();

    public String getRequestId() {
        return requestId.get();
    }

    public void setRequestId(String id) {
        requestId.set(id);
    }

    public void clear() {
        requestId.remove();
    }
}
