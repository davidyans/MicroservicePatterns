package com.bookstore.order_query.controllers;

import com.bookstore.order_query.domain.OrderHistory;
import com.bookstore.order_query.repository.OrderHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

    // Ejemplo de filtro por rango de fechas y status
    // GET /api/order-history/filter?start=2025-01-01T00:00:00&end=2025-01-31T23:59:59&status=CREATED
    @GetMapping("/filter")
    public List<OrderHistory> filterOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) String status
    ) {
        // Podrías implementar un método custom en el repositorio (ej: using @Query o Specifications).
        // Por simplicidad, se retorna todo si no hay un method custom.

        // Ejemplo: if tienes un método:
        //   List<OrderHistory> findByOrderDateBetweenAndStatus(LocalDateTime start, LocalDateTime end, String status);
        // podrías llamarlo directo. Algo como:

        // if (start != null && end != null && status != null) {
        //     return orderHistoryRepository.findByOrderDateBetweenAndStatus(start, end, status);
        // } else ...
        return orderHistoryRepository.findAll(); // Placeholder
    }
}
