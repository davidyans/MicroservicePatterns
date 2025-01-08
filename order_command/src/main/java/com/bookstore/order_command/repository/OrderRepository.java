package com.bookstore.order_command.repository;

import com.bookstore.order_command.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
