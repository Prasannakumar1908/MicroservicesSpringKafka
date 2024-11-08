package com.prodify.cqrs.OrderService.command.kafka;

import com.prodify.cqrs.OrderService.command.api.kafka.OrderKafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

public class OrderKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private OrderKafkaProducer orderKafkaProducer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendOrderEvent() {
        String topic = "order-events";
        String orderData = "Order data";

        orderKafkaProducer.sendOrderEvent(topic, orderData);

        verify(kafkaTemplate, times(1)).send(topic, orderData);
    }
}
