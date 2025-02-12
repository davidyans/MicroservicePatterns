package com.bookstore.order_query.controllers;

import com.bookstore.order_query.domain.OrderHistory;
import com.bookstore.order_query.repository.OrderHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/order-history")
public class OrderHistoryController {

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @GetMapping
    public List<OrderHistory> getAllOrders() {
        return orderHistoryRepository.findAll();
    }

    @GetMapping("/{orderId}")
    public OrderHistory getOrderById(@PathVariable Integer orderId) {
        return orderHistoryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("No se encontró la orden con ID: " + orderId));
    }
}
