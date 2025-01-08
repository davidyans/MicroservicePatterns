package com.bookstore.order_query.repository;

import com.bookstore.order_query.domain.OrderHistoryDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryDetailRepository extends JpaRepository<OrderHistoryDetail, Long> {
}
