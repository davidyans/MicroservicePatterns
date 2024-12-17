package com.bookstore.microservice.order.services;

import com.david.bookstore.microservice.order.domain.Order;
import com.david.bookstore.microservice.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public Optional<Order> getOrderById(int orderId) {
        return Optional.ofNullable(orderRepository.getOrderById(orderId));
    }

    public Order createOrder(Order order) {
        return orderRepository.createOrder(order);
    }

    public Order updateOrder(Order order) {
        return orderRepository.updateOrder(order);
    }

    public void deleteOrder(int orderId) {
        orderRepository.deleteOrder(orderId);
    }
}
