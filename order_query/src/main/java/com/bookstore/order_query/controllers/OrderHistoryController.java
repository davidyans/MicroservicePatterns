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


    // Nuevo Endpoint: Filtrar órdenes por fecha de inicio
    // GET /api/order-history/filterByDate?start=2025-01-01T00:00:00
    @GetMapping("/filterByDate")
    public ResponseEntity<List<OrderHistory>> filterOrdersByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start
    ) {
        // Validar que la fecha de inicio no sea nula y no esté en el futuro
        if (start == null) {
            return ResponseEntity.badRequest().body(null);
        }
        if (start.isAfter(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(null);
        }

        List<OrderHistory> orders = orderHistoryRepository.findByOrderDateAfter(start);
        return ResponseEntity.ok(orders);
    }

    // Nuevo Endpoint: Filtrar órdenes por estado
    // GET /api/order-history/filterByStatus?status=CREATED
    @GetMapping("/filterByStatus")
    public ResponseEntity<List<OrderHistory>> filterOrdersByStatus(
            @RequestParam String status
    ) {
        // Validar que el estado no sea nulo o vacío
        if (status == null || status.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        List<OrderHistory> orders = orderHistoryRepository.findByStatus(status);
        return ResponseEntity.ok(orders);
    }

    // Opcional: Endpoint combinado para filtrar por fecha de inicio y estado
    // GET /api/order-history/filter?start=2025-01-01T00:00:00&status=CREATED
    @GetMapping("/filter")
    public ResponseEntity<List<OrderHistory>> filterOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) String status
    ) {
        if (start != null && status != null) {
            // Filtrar por fecha de inicio y estado
            List<OrderHistory> orders = orderHistoryRepository.findByOrderDateAfterAndStatus(start, status);
            return ResponseEntity.ok(orders);
        } else if (start != null) {
            // Filtrar solo por fecha de inicio
            List<OrderHistory> orders = orderHistoryRepository.findByOrderDateAfter(start);
            return ResponseEntity.ok(orders);
        } else if (status != null) {
            // Filtrar solo por estado
            List<OrderHistory> orders = orderHistoryRepository.findByStatus(status);
            return ResponseEntity.ok(orders);
        } else {
            // Si no se proporcionan filtros, devolver todas las órdenes
            return ResponseEntity.ok(orderHistoryRepository.findAll());
        }
    }
}
