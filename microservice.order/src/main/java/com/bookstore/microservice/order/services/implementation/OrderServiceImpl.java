package com.bookstore.microservice.order.services.implementation;

import com.bookstore.microservice.order.domain.Order;
import com.bookstore.microservice.order.domain.OrderDetail;
import com.bookstore.microservice.order.dto.OrderDTO;
import com.bookstore.microservice.order.dto.OrderDetailDTO;
import com.bookstore.microservice.order.exceptions.ResourceNotFoundException;
import com.bookstore.microservice.order.repository.OrderRepository;
import com.bookstore.microservice.order.services.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        return convertToDTO(order);
    }

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setUserId(orderDTO.getUserId());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setTotal(calculateTotal(orderDTO.getOrderDetails()));
        order.setUpdatedDate(LocalDateTime.now());

        return convertToDTO(orderRepository.save(order));
    }

    @Override
    public OrderDTO updateOrder(Integer orderId, OrderDTO orderDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        order.setStatus(orderDTO.getStatus());
        order.setUpdatedDate(LocalDateTime.now());

        return convertToDTO(orderRepository.save(order));
    }

    @Override
    public void deleteOrder(Integer orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order not found with ID: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    private BigDecimal calculateTotal(List<OrderDetailDTO> details) {
        return details.stream()
                .map(d -> d.getUnitPrice().multiply(BigDecimal.valueOf(d.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUserId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotal(order.getTotal());
        return dto;
    }
}
