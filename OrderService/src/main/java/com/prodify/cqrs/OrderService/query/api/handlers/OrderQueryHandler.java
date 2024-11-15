//package com.prodify.cqrs.OrderService.query.api.handlers;
//
//import com.prodify.cqrs.OrderService.command.api.model.OrderRestModel;
//import com.prodify.cqrs.OrderService.query.api.data.Order;
//import com.prodify.cqrs.OrderService.query.api.data.OrderRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Component;
//
//import jakarta.persistence.EntityNotFoundException;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//@Slf4j
//public class OrderQueryHandler {
//
//    private final OrderRepository orderRepository;
//
//    @Autowired
//    public OrderQueryHandler(OrderRepository orderRepository) {
//        this.orderRepository = orderRepository;
//    }
//
//    // Query to fetch a specific order by orderId
//    public OrderRestModel getOrderById(String orderId) {
//        log.info("Fetching order with ID: {}", orderId);
//        Order order = orderRepository.findById(orderId).orElseThrow(() ->
//                new EntityNotFoundException("Order with ID " + orderId + " not found"));
//
//        return convertToOrderRestModel(order);
//    }
//
//    // Query to fetch all orders with pagination and sorting
//    public List<OrderRestModel> getAllOrders(int page, int size, String sortBy, String direction) {
//        log.info("Fetching all orders with pagination. Page: {}, Size: {}, SortBy: {}, Direction: {}", page, size, sortBy, direction);
//
//        Sort sort = Sort.by(Sort.Order.by(sortBy).with(Sort.Direction.fromString(direction)));
//        List<Order> orders = orderRepository.findAll(PageRequest.of(page, size, sort)).getContent();
//
//        return orders.stream()
//                .map(this::convertToOrderRestModel)
//                .collect(Collectors.toList());
//    }
//
//    // Search orders with dynamic filtering, pagination, and sorting
//    public List<OrderRestModel> searchOrders(String productId, String userId, int page, int size, String sortBy, String direction) {
//        log.info("Searching orders with filters - ProductId: {}, UserId: {}, Page: {}, Size: {}, SortBy: {}, Direction: {}",
//                productId, userId, page, size, sortBy, direction);
//
//        // You can extend this logic to add more filtering criteria as needed
//        Sort sort = Sort.by(Sort.Order.by(sortBy).with(Sort.Direction.fromString(direction)));
//        List<Order> orders = orderRepository.findAll(PageRequest.of(page, size, sort))
//                .getContent()
//                .stream()
//                .filter(order -> (productId == null || order.getProductId().equals(productId)) &&
//                        (userId == null || order.getUserId().equals(userId)))
//                .collect(Collectors.toList());
//
//        return orders.stream()
//                .map(this::convertToOrderRestModel)
//                .collect(Collectors.toList());
//    }
//
//    // Convert entity to model
//    private OrderRestModel convertToOrderRestModel(Order order) {
//        OrderRestModel model = new OrderRestModel();
//        model.setOrderId(order.getOrderId());
//        model.setProductId(order.getProductId());
//        model.setUserId(order.getUserId());
//        model.setAddressId(order.getAddressId());
//        model.setQuantity(order.getQuantity());
//        model.setOrderStatus(order.getOrderStatus());
//        return model;
//    }
//}
