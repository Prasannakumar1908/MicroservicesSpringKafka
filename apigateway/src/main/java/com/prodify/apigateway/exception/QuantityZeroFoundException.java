package com.prodify.apigateway.exception;

public class QuantityZeroFoundException extends RuntimeException {
    private String message;
    public QuantityZeroFoundException() {}
    public QuantityZeroFoundException(String msg) {
        super(msg);
        this.message = msg;
    }
}
