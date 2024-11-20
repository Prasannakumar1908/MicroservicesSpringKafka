package com.prodify.cqrs.OrderService.command.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodify.cqrs.OrderService.command.api.kafka.OrderKafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private OrderKafkaProducer orderKafkaProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderKafkaProducer = new OrderKafkaProducer(kafkaTemplate, objectMapper);
    }

    @Test
    void testSendOrderEvent_Success() throws JsonProcessingException {
        // Arrange
        String topic = "test-topic";
        String orderId = "order123";
        Object orderRestModel = new Object(); // Mock object
        String serializedOrderData = "{\"mock\": \"data\"}";
        String expectedMessage = "{\"orderId\": \"order123\", \"orderData\": " + serializedOrderData + "}";

        when(objectMapper.writeValueAsString(orderRestModel)).thenReturn(serializedOrderData);

        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(eq(topic), eq(expectedMessage))).thenReturn(future);

        // Act
        orderKafkaProducer.sendOrderEvent(topic, orderId, orderRestModel);

        // Assert
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate, times(1)).send(eq(topic), captor.capture());
        assertEquals(expectedMessage, captor.getValue());
    }

    @Test
    void testSendOrderEvent_Failure() throws JsonProcessingException {
        // Arrange
        String topic = "test-topic";
        String orderId = "order123";
        Object orderRestModel = new Object(); // Mock object
        String serializedOrderData = "{\"mock\": \"data\"}";
        String expectedMessage = "{\"orderId\": \"order123\", \"orderData\": " + serializedOrderData + "}";

        when(objectMapper.writeValueAsString(orderRestModel)).thenReturn(serializedOrderData);

        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka error"));
        when(kafkaTemplate.send(eq(topic), eq(expectedMessage))).thenReturn(future);

        // Act
        orderKafkaProducer.sendOrderEvent(topic, orderId, orderRestModel);

        // Assert
        verify(kafkaTemplate, times(1)).send(eq(topic), eq(expectedMessage));
    }

    @Test
    void testSendOrderEvent_JsonProcessingException() throws JsonProcessingException {
        // Arrange
        String topic = "test-topic";
        String orderId = "order123";
        Object orderRestModel = new Object();

        when(objectMapper.writeValueAsString(orderRestModel)).thenThrow(new JsonProcessingException("Serialization error") {});

        // Act
        orderKafkaProducer.sendOrderEvent(topic, orderId, orderRestModel);

        // Assert
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }
}
