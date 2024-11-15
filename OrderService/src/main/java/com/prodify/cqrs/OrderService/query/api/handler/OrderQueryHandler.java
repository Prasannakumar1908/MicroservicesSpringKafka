package com.prodify.cqrs.OrderService.query.api.handler;

import com.prodify.cqrs.OrderService.command.api.data.Order;
import com.prodify.cqrs.OrderService.command.api.model.OrderRestModel;
import com.prodify.cqrs.OrderService.command.api.data.OrderRepository;
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

    @Autowired
    public OrderQueryHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Query to fetch a specific order by orderId
    public OrderRestModel getOrderById(String orderId) {
        log.info("Fetching order with ID: {}", orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new EntityNotFoundException("Order with ID " + orderId + " not found"));

        return convertToOrderRestModel(order);
    }

    // Query to fetch all orders
    public List<OrderRestModel> getAllOrders() {
        log.info("Fetching all order ");

        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(this::convertToOrderRestModel)
                .collect(Collectors.toList());
    }
    // New method to search orders based on dynamic criteria (filtering, pagination, sorting)
    public List<OrderRestModel> searchOrders(String productId, String userId, String addressId,
                                             Integer quantityMin, Integer quantityMax, int page, int size,
                                             String sortBy, String direction) {

        // Prepare sorting
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(Sort.Order.by(sortBy).with(sortDirection));
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        // Query the database with the provided filters and pagination
        List<Order> orders = orderRepository.findAllByFilters(productId, userId, addressId, quantityMin, quantityMax, pageRequest);

        // Convert the results into OrderRestModel
        return orders.stream()
                .map(this::convertToOrderRestModel)
                .collect(Collectors.toList());
    }
    // Convert entity to model
    private OrderRestModel convertToOrderRestModel(Order order) {
        OrderRestModel model = new OrderRestModel();
        model.setProductId(order.getProductId());
        model.setUserId(order.getUserId());
        model.setAddressId(order.getAddressId());
        model.setQuantity(order.getQuantity());
        return model;
    }
}
