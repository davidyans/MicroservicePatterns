package com.bookstore.microservice.order.repository;

import com.bookstore.microservice.order.domain.OrderDetail;
import com.bookstore.microservice.order.domain.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
}
