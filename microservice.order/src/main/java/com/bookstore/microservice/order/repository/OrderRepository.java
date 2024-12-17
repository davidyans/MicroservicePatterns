package com.bookstore.microservice.order.repository;

import com.bookstore.microservice.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
