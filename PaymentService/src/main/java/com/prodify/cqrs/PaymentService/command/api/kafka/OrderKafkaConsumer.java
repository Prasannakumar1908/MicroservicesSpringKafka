package com.prodify.cqrs.PaymentService.command.api.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prodify.cqrs.PaymentService.command.api.events.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderKafkaConsumer.class);
    private final ObjectMapper objectMapper;

    public OrderKafkaConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order-events", groupId = "payment-service-group")
    public void listenOrderEvents(String orderData) {
        try {
            // Log the raw Kafka message that was sent (including orderId and orderData)
            log.info("Received raw Order Event from Kafka: {}", orderData);

            // Deserialize the message into a JsonNode to extract both orderId and orderData
            JsonNode jsonNode = objectMapper.readTree(orderData);
            String orderId = jsonNode.get("orderId").asText();  // Extract orderId from the message
            JsonNode orderDataNode = jsonNode.get("orderData"); // Extract the orderData field

            // Check if orderDataNode is not null before proceeding
            if (orderDataNode != null) {
                // Deserialize the orderData part into the OrderEvent object
                OrderEvent orderEvent = objectMapper.treeToValue(orderDataNode, OrderEvent.class);

                // Log the extracted details from the message
                log.info("Received Order Event in Payment Service with orderId: {}, addressId: {}, quantity: {}, userId: {}, productId: {}, orderStatus: {}, requestId: {}",
                        orderEvent.getOrderId(),
                        orderEvent.getAddressId(),
                        orderEvent.getQuantity(),
                        orderEvent.getUserId(),
                        orderEvent.getProductId(),
                        orderEvent.getOrderStatus(),
                        orderEvent.getRequestId());

                // Create and process the payment event
                processPaymentEvent(orderEvent);
            } else {
                log.warn("Received Kafka message with no orderData field: {}", orderData);
            }

        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize order event from Kafka", e);
        }
    }

    private void processPaymentEvent(OrderEvent orderEvent) {
        log.info("Processing payment for Order ID: {} with Quantity: {}", orderEvent.getOrderId(), orderEvent.getQuantity());
        // Simulate payment logic
        // You can add further logic for interacting with databases or payment gateways.
    }
}
