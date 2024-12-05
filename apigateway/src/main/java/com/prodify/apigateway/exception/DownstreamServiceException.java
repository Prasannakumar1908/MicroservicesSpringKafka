package com.prodify.apigateway.exception;

import org.springframework.http.HttpStatusCode;

public class DownstreamServiceException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public DownstreamServiceException(HttpStatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
