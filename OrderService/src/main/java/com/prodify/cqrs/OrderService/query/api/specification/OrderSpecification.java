package com.prodify.cqrs.OrderService.query.api.specification;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderSpecification implements Specification<Order> {
    private final Map<String, Object> criteria;

    public OrderSpecification(Map<String, Object> criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        // Dynamically create predicates based on the provided criteria
        criteria.forEach((key, value) -> {
            if ("productId".equals(key) && value != null) {
                predicates.add(cb.equal(root.get("productId"), value));
            }
            if ("userId".equals(key) && value != null) {
                predicates.add(cb.equal(root.get("userId"), value));
            }
            if ("addressId".equals(key) && value != null) {
                predicates.add(cb.equal(root.get("addressId"), value));
            }
            if ("quantityMin".equals(key) && value != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("quantity"), (Integer) value));
            }
            if ("quantityMax".equals(key) && value != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("quantity"), (Integer) value));
            }
            // Add more criteria checks here as needed
        });

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
