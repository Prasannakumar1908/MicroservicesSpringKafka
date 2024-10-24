package com.prodify.cqrs.PaymentService.command.api.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentKafkaConsumer {

    @KafkaListener(topics = "order-events", groupId = "payment-service-group")
    public void listenOrderEvents(String orderData) {
        System.out.println("Received Order Event in Payment Service: " + orderData);
        // Implement payment processing logic here
    }
}
