package com.prodify.cqrs.OrderService.command.api.data;

import com.prodify.cqrs.OrderService.command.api.data.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    // Custom query method with dynamic filtering and pagination
    @Query("SELECT o FROM Order o WHERE " +
            "(:productId IS NULL OR o.productId = :productId) AND " +
            "(:userId IS NULL OR o.userId = :userId) AND " +
            "(:addressId IS NULL OR o.addressId = :addressId) AND " +
            "(:quantityMin IS NULL OR o.quantity >= :quantityMin) AND " +
            "(:quantityMax IS NULL OR o.quantity <= :quantityMax)")
    List<Order> findAllByFilters(String productId, String userId, String addressId,
                                 Integer quantityMin, Integer quantityMax, Pageable pageable);
}
