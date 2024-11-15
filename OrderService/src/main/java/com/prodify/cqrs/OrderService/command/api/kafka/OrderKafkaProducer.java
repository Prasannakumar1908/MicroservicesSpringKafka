//package com.prodify.cqrs.OrderService.command.api.kafka;
//
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//public class OrderKafkaProducer {
//
//    private final KafkaTemplate<String, String> kafkaTemplate;
//
//    public OrderKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    public void sendOrderEvent(String topic, String orderData) {
//        kafkaTemplate.send(topic, orderData);
//        System.out.println("Order event sent to Kafka: " + orderData);
//    }
//}

package com.prodify.cqrs.OrderService.command.api.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderKafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderEvent(String topic, String orderData) {
        log.info("Sending order event to Kafka topic: {}", topic);

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, orderData);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Order event sent successfully to Kafka. Topic: {}, Offset: {}, Order Data: {}",
                        topic, result.getRecordMetadata().offset(), orderData);
            } else {
                log.error("Failed to send order event to Kafka. Topic: {}, Order Data: {}", topic, orderData, ex);
            }
        });
    }
}

