//package com.prodify.cqrs.OrderService.command.api.saga;
//
//import com.prodify.cqrs.CommonService.commands.*;
//import com.prodify.cqrs.CommonService.events.*;
//import com.prodify.cqrs.CommonService.model.User;
//import com.prodify.cqrs.CommonService.queries.GetUserPaymentDetailsQuery;
//import com.prodify.cqrs.OrderService.command.api.events.OrderCreatedEvent;
//import lombok.extern.slf4j.Slf4j;
//import org.axonframework.commandhandling.gateway.CommandGateway;
//import org.axonframework.messaging.responsetypes.ResponseTypes;
//import org.axonframework.modelling.saga.EndSaga;
//import org.axonframework.modelling.saga.SagaEventHandler;
//import org.axonframework.modelling.saga.StartSaga;
//import org.axonframework.queryhandling.QueryGateway;
//import org.axonframework.spring.stereotype.Saga;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.UUID;
//
//@Saga
//@Slf4j
//public class OrderProcessingSaga {
//
//    @Autowired
//    private transient CommandGateway commandGateway;
//
//    @Autowired
//    private transient QueryGateway queryGateway;
//
//
//    public OrderProcessingSaga() {
//    }
//
//    @StartSaga
//    @SagaEventHandler(associationProperty = "orderId")
//    private void handle(OrderCreatedEvent event) {
//        log.info("Starting saga for OrderCreatedEvent with Order ID: {}", event.getOrderId());
//
//
//        GetUserPaymentDetailsQuery getUserPaymentDetailsQuery
//                = new GetUserPaymentDetailsQuery(event.getUserId());
//
//        User user = null;
//
//        try {
//            user = queryGateway.query(getUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
//        } catch (Exception e) {
//            log.error("Error retrieving user payment details: {}", e.getMessage());
//            cancelOrderCommand(event.getOrderId());
//            return;  // Exit if user is null
//        }
//
//        if (user == null || user.getCardDetails() == null) {
//            log.error("User or CardDetails is null for userId: {}", event.getUserId());
//            cancelOrderCommand(event.getOrderId());
//            return;
//        }
//
//        ValidatePaymentCommand validatePaymentCommand
//                = ValidatePaymentCommand
//                .builder()
//                .cardDetails(user.getCardDetails())
//                .orderId(event.getOrderId())
//                .paymentId(UUID.randomUUID().toString())
//                .build();
//
//        commandGateway.sendAndWait(validatePaymentCommand);
//    }
//
//    private void cancelOrderCommand(String orderId) {
//        log.info("Sending CancelOrderCommand for Order ID: {}", orderId);
//        CancelOrderCommand cancelOrderCommand
//                = new CancelOrderCommand(orderId);
//        commandGateway.send(cancelOrderCommand);
//    }
//
//    @SagaEventHandler(associationProperty = "orderId")
//    private void handle(PaymentProcessedEvent event) {
//        log.info("PaymentProcessedEvent in Saga for Order Id : {}",
//                event.getOrderId());
//        try {
//
//            if(true)
//                throw new Exception();
//
//            ShipOrderCommand shipOrderCommand
//                    = ShipOrderCommand
//                    .builder()
//                    .shipmentId(UUID.randomUUID().toString())
//                    .orderId(event.getOrderId())
//                    .build();
//            commandGateway.send(shipOrderCommand);
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            // Start the compensating transaction
//            cancelPaymentCommand(event);
//        }
//    }
//
//    private void cancelPaymentCommand(PaymentProcessedEvent event) {
//        log.info("Initializing CancelPaymentCommand for Payment ID: {} and Order ID:{}", event.getPaymentId(), event.getOrderId());
//        CancelPaymentCommand cancelPaymentCommand
//                = new CancelPaymentCommand(
//                event.getPaymentId(), event.getOrderId()
//        );
//
//        commandGateway.send(cancelPaymentCommand);
//    }
//
//    @SagaEventHandler(associationProperty = "orderId")
//    public void handle(OrderShippedEvent event) {
//
//        log.info("Processing OrderShippedEvent for Order Id : {}",
//                event.getOrderId());
//
//        CompleteOrderCommand completeOrderCommand
//                = CompleteOrderCommand.builder()
//                .orderId(event.getOrderId())
//                .orderStatus("APPROVED")
//                .build();
//
//        commandGateway.send(completeOrderCommand);
//    }
//
//    @SagaEventHandler(associationProperty = "orderId")
//    @EndSaga
//    public void handle(OrderCompletedEvent event) {
//        log.info("OrderCompletedEvent received for Order Id : {}",
//                event.getOrderId());
//    }
//
//    @SagaEventHandler(associationProperty = "orderId")
//    @EndSaga
//    public void handle(OrderCancelledEvent event) {
//        log.info("OrderCancelledEvent received for Order Id : {}",
//                event.getOrderId());
//    }
//
//    @SagaEventHandler(associationProperty = "orderId")
//    public void handle(PaymentCancelledEvent event) {
//        log.info("PaymentCancelledEvent received for Order Id : {}",
//                event.getOrderId());
//        cancelOrderCommand(event.getOrderId());
//    }
//}

package com.prodify.cqrs.OrderService.command.api.saga;

import com.prodify.cqrs.CommonService.commands.*;
import com.prodify.cqrs.CommonService.events.*;
import com.prodify.cqrs.CommonService.model.User;
import com.prodify.cqrs.CommonService.queries.GetUserPaymentDetailsQuery;
import com.prodify.cqrs.OrderService.command.api.events.OrderCreatedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Saga
@Slf4j
@AllArgsConstructor
public class OrderProcessingSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient QueryGateway queryGateway;

    public OrderProcessingSaga() {
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent event) {
        log.info("Starting saga for OrderCreatedEvent with Order ID: {} with requestId:{}", event.getOrderId(),event.getRequestId());

        GetUserPaymentDetailsQuery getUserPaymentDetailsQuery = new GetUserPaymentDetailsQuery(event.getUserId());
        User user=null;

        try {
            user = queryGateway.query(getUserPaymentDetailsQuery, ResponseTypes.instanceOf(User.class)).join();
            if (user == null || user.getCardDetails() == null) {
                log.error("User or CardDetails is null for userId: {}", event.getUserId());
                cancelOrderCommand(event.getOrderId());
                return;
            }
        } catch (Exception e) {
            log.error("Error retrieving user payment details: {}", e.getMessage(), e);
            cancelOrderCommand(event.getOrderId());
            return;
        }

        ValidatePaymentCommand validatePaymentCommand = ValidatePaymentCommand.builder()
                .cardDetails(user.getCardDetails())
                .orderId(event.getOrderId())
                .paymentId(UUID.randomUUID().toString())
                .build();

        try {
            commandGateway.sendAndWait(validatePaymentCommand);
            log.info("ValidatePaymentCommand sent for Order ID: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to send ValidatePaymentCommand for Order ID: {} with requestId:{}", event.getOrderId(),event.getRequestId(), e);
            cancelOrderCommand(event.getOrderId());
        }
    }

    private void cancelOrderCommand(String orderId) {
        log.info("Sending CancelOrderCommand for Order ID: {}", orderId);
        CancelOrderCommand cancelOrderCommand = new CancelOrderCommand(orderId);
        commandGateway.send(cancelOrderCommand, (message, ex) -> {
            if (ex != null) {
                log.error("Failed to send CancelOrderCommand for Order ID: {}", orderId, ex);
            } else {
                log.info("CancelOrderCommand sent successfully for Order ID: {}", orderId);
            }
        });
    }
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentProcessedEvent event) {
        log.info("PaymentProcessedEvent in Saga for Order Id : {}", event.getOrderId());
        try {
            ShipOrderCommand shipOrderCommand = ShipOrderCommand.builder()
                    .shipmentId(UUID.randomUUID().toString())
                    .orderId(event.getOrderId())
                    .build();
            commandGateway.send(shipOrderCommand, (message, ex) -> {
                if (ex != null) {
                    log.error("Failed to send ShipOrderCommand for Order ID: {}", event.getOrderId(), ex);
                    cancelPaymentCommand(event);
                } else {
                    log.info("ShipOrderCommand sent successfully for Order ID: {}", event.getOrderId());
                }
            });
        } catch (Exception e) {
            log.error("Exception occurred during PaymentProcessedEvent handling for Order ID: {}", event.getOrderId(), e);
            cancelPaymentCommand(event);
        }
    }

    private void cancelPaymentCommand(PaymentProcessedEvent event) {
        log.info("Initializing CancelPaymentCommand for Payment ID: {} and Order ID: {}", event.getPaymentId(), event.getOrderId());
        CancelPaymentCommand cancelPaymentCommand = new CancelPaymentCommand(event.getPaymentId(), event.getOrderId());
        commandGateway.send(cancelPaymentCommand, (message, ex) -> {
            if (ex != null) {
                log.error("Failed to send CancelPaymentCommand for Payment ID: {} and Order ID: {}", event.getPaymentId(), event.getOrderId(), ex);
            } else {
                log.info("CancelPaymentCommand sent successfully for Payment ID: {} and Order ID: {}", event.getPaymentId(), event.getOrderId());
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderShippedEvent event) {
        log.info("Processing OrderShippedEvent for Order ID: {}", event.getOrderId());
        CompleteOrderCommand completeOrderCommand = CompleteOrderCommand.builder()
                .orderId(event.getOrderId())
                .orderStatus("APPROVED")
                .build();

        commandGateway.send(completeOrderCommand, (message, ex) -> {
            if (ex != null) {
                log.error("Failed to send CompleteOrderCommand for Order ID: {}", event.getOrderId(), ex);
            } else {
                log.info("CompleteOrderCommand sent successfully for Order ID: {}", event.getOrderId());
            }
        });
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void handle(OrderCompletedEvent event) {
        log.info("OrderCompletedEvent received, ending saga for Order ID: {}", event.getOrderId());
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void handle(OrderCancelledEvent event) {
        log.info("OrderCancelledEvent received, ending saga for Order ID: {}", event.getOrderId());
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(PaymentCancelledEvent event) {
        log.info("PaymentCancelledEvent received for Order ID: {}", event.getOrderId());
        cancelOrderCommand(event.getOrderId());
    }
}
