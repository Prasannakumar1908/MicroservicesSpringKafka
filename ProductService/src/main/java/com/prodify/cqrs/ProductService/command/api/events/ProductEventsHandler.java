package com.prodify.cqrs.ProductService.command.api.events;

import com.prodify.cqrs.ProductService.command.api.data.Product;
import com.prodify.cqrs.ProductService.command.api.data.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@ProcessingGroup("product")
@Slf4j
public class ProductEventsHandler {

//    private static final Logger logger = (Logger) LoggerFactory.getLogger(ProductEventsHandler.class);

    private ProductRepository productRepository;

    public ProductEventsHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @EventHandler
    public void on(ProductCreatedEvent event) throws Exception {
        Product product =
                new Product();
        BeanUtils.copyProperties(event,product);
        productRepository.save(product);
        log.info("Product created with ID:{}",event.getProductId());
//        logger.info("Product created with ID: {}",event.getProductId());

        throw new Exception("Exception Occurred");
    }

    @ExceptionHandler
    public void handle(Exception exception) throws Exception {
        log.error("Exception in EventHandler: {}", exception.getMessage(),exception);
//        logger.error("Exception in EventHandler: {}", exception.getMessage(), exception);
        throw exception;
    }
}
