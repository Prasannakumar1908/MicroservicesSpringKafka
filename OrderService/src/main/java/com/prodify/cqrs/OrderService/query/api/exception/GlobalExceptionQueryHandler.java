//package com.prodify.cqrs.OrderService.query.api.exception;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//
//@ControllerAdvice
//public class GlobalExceptionQueryHandler {
//
//    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionQueryHandler.class);
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<String> handleException(Exception e) {
//        logger.error("An unexpected error occurred: {}", e.getMessage());
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body("An unexpected error occurred. Please try again later.");
//    }
//}
