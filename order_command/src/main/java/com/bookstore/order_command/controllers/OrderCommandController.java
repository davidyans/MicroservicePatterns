package com.bookstore.order_command.controllers;

import com.bookstore.order_command.dto.OrderDTO;
import com.bookstore.order_command.services.OrderCommandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders-command")
public class OrderCommandController {

    @Autowired
    private OrderCommandService orderCommandService;

    // Crear orden
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO created = orderCommandService.createOrder(orderDTO);
        return ResponseEntity.ok(created);
    }

    // Otros endpoints de CUD si lo necesitas...
    // (Por ahora no implementamos updateOrder ni getAllOrders,
    //  pues la idea es que la lectura la haga el Query Service)
}

