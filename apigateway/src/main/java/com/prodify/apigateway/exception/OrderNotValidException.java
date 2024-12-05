package com.prodify.apigateway.exception;

public class OrderNotValidException extends RuntimeException{
    private String message;
    public OrderNotValidException() {}
    public OrderNotValidException(String msg) {
        super(msg);
        this.message = msg;
    }
}