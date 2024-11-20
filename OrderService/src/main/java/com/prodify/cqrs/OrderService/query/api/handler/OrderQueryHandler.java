package com.prodify.cqrs.OrderService.query.api.handler;

import com.prodify.cqrs.OrderService.command.api.data.Order;
import com.prodify.cqrs.OrderService.command.api.model.OrderRestModel;
import com.prodify.cqrs.OrderService.command.api.data.OrderRepository;
import com.prodify.cqrs.OrderService.command.api.util.RequestIdContext;
import com.prodify.cqrs.OrderService.query.api.exception.OrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OrderQueryHandler {

    private final OrderRepository orderRepository;
    private final RequestIdContext requestIdContext;

    @Autowired
    public OrderQueryHandler(OrderRepository orderRepository, RequestIdContext requestIdContext) {
        this.orderRepository = orderRepository;
        this.requestIdContext = requestIdContext;
    }

    // Query to fetch a specific order by orderId
    public OrderRestModel getOrderById(String orderId) {
        String requestId = requestIdContext.getRequestId();
        log.debug("Received request to fetch order details for orderId: {} with requestId: {}", orderId, requestId);
        try {
            Order order = orderRepository.findById(orderId).orElseThrow(() ->
                    new OrderNotFoundException("Order with ID " + orderId + " not found"));

            log.info("Successfully retrieved order with ID: {} for requestId: {}", orderId, requestId);
            return convertToOrderRestModel(order);

        } catch (OrderNotFoundException e) {
            log.warn("Order with ID: {} not found. RequestId: {}", orderId, requestId);
            throw e;  // rethrow exception after logging
        } catch (Exception e) {
            log.error("Error fetching order with ID: {} for requestId: {}", orderId, requestId, e);
            throw new RuntimeException("Error occurred while fetching the order", e);  // rethrow exception after logging
        }
    }

    // Query to fetch all orders
    public List<OrderRestModel> getAllOrders() {
        String requestId = requestIdContext.getRequestId();
        log.debug("Received request to fetch all orders with requestId: {}", requestId);

        try {
            List<Order> orders = orderRepository.findAll();
            log.info("Successfully retrieved {} orders for requestId: {}", orders.size(), requestId);
            return orders.stream()
                    .map(this::convertToOrderRestModel)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching all orders for requestId: {}", requestId, e);
            throw new RuntimeException("Error occurred while fetching all orders", e);  // rethrow exception after logging
        }

    }
    // New method to search orders based on dynamic criteria (filtering, pagination, sorting)
    public List<OrderRestModel> searchOrders(String productId, String userId, String addressId,
                                             Integer quantityMin, Integer quantityMax, int page, int size,
                                             String sortBy, String direction) {

        String requestId = requestIdContext.getRequestId();
        log.debug("Received search request with filters: ProductId: {}, UserId: {}, AddressId: {}, " +
                "QuantityMin: {}, QuantityMax: {}, Page: {}, Size: {}, SortBy: {}, Direction: {} " +
                "with requestId: {}", productId, userId, addressId, quantityMin, quantityMax, page, size, sortBy, direction, requestId);

        // Validate search parameters
        if (quantityMin != null && quantityMax != null && quantityMin > quantityMax) {
            log.warn("Invalid search request: quantityMin cannot be greater than quantityMax. RequestId: {}", requestId);
            throw new IllegalArgumentException("quantityMin cannot be greater than quantityMax");
        }

        // Prepare sorting
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(Sort.Order.by(sortBy).with(sortDirection));
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        try {
            // Query the database with the provided filters and pagination
            List<Order> orders = orderRepository.findAllByFilters(productId, userId, addressId, quantityMin, quantityMax, pageRequest);

            log.info("Successfully retrieved {} orders based on search criteria with requestId: {}", orders.size(), requestId);

            // Convert the results into OrderRestModel
            return orders.stream()
                    .map(this::convertToOrderRestModel)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error during order search with requestId: {}", requestId, e);
            throw new RuntimeException("Error occurred during the search process", e);  // rethrow exception after logging
        }
    }
    // Convert entity to model
    private OrderRestModel convertToOrderRestModel(Order order) {
        log.debug("Converting OrderEntity to OrderRestModel: {}", order);
        OrderRestModel model = new OrderRestModel();
        model.setProductId(order.getProductId());
        model.setUserId(order.getUserId());
        model.setAddressId(order.getAddressId());
        model.setQuantity(order.getQuantity());
        return model;
    }
}
