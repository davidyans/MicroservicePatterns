package com.bookstore.order_command.controllers;

import com.bookstore.order_command.dto.OrderDTO;
import com.bookstore.order_command.services.OrderCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders-command")
@CrossOrigin(origins = "*")
public class OrderCommandController {

    @Autowired
    private OrderCommandService orderCommandService;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO created = orderCommandService.createOrder(orderDTO);
        return ResponseEntity.ok(created);
    }
}

