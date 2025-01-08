package com.bookstore.order_command.repository;


import com.bookstore.order_command.domain.OrderDetail;
import com.bookstore.order_command.domain.OrderDetailId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderDetailId> {
    List<OrderDetail> findByOrder_OrderId(Integer orderId);
    void deleteAllByOrder_OrderId(Integer orderId);
}
