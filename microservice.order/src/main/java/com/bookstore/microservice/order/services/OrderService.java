package com.bookstore.microservice.order.services;

import com.bookstore.microservice.order.dto.OrderDTO;

import java.util.List;

public interface OrderService {
    List<OrderDTO> getAllOrders();
    OrderDTO getOrderById(Integer orderId);
    OrderDTO createOrder(OrderDTO orderDTO);
    OrderDTO updateOrder(Integer orderId, OrderDTO orderDTO);
    void deleteOrder(Integer orderId);
}
