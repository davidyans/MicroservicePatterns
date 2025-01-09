package com.bookstore.order_query.repository;

import com.bookstore.order_query.domain.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    Optional<OrderHistory> findByOrderId(Integer orderId);
    List<OrderHistory> findByOrderDateAfter(LocalDateTime startDate);
    List<OrderHistory> findByStatus(String status);
    List<OrderHistory> findByOrderDateAfterAndStatus(LocalDateTime startDate, String status);
}
