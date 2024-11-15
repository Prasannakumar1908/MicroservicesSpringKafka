package com.prodify.cqrs.OrderService.command.api.data;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    @Query("SELECT o FROM Order o WHERE " +
            "(o.productId LIKE %:productId% OR :productId IS NULL) AND " +
            "(o.userId LIKE %:userId% OR :userId IS NULL) AND " +
            "(o.addressId LIKE %:addressId% OR :addressId IS NULL) AND " +
            "(o.quantity >= :quantityMin OR :quantityMin IS NULL) AND " +
            "(o.quantity <= :quantityMax OR :quantityMax IS NULL)")
    List<Order> findAllByFilters(String productId, String userId, String addressId,
                                 Integer quantityMin, Integer quantityMax, PageRequest pageRequest);

}