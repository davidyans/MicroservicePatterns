package com.bookstore.order_query.repository;

import com.bookstore.order_query.domain.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    Optional<OrderHistory> findByOrderId(Integer orderId);
}
