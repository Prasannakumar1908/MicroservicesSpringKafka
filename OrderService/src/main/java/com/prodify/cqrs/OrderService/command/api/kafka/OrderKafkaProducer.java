package com.prodify.cqrs.OrderService.command.api.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderEvent(String topic, String orderData) {
        kafkaTemplate.send(topic, orderData);
        System.out.println("Order event sent to Kafka: " + orderData);
    }
}
