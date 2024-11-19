package com.prodify.cqrs.PaymentService.command.api.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public PaymentEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    // Send payment success/failure event to Kafka
    public void sendPaymentEvent(String topic, String paymentStatus) {
        kafkaTemplate.send(topic, paymentStatus);
    }
}
