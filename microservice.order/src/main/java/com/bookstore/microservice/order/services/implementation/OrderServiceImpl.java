package com.bookstore.microservice.order.services.implementation;

import com.bookstore.microservice.order.domain.Order;
import com.bookstore.microservice.order.dto.OrderDTO;
import com.bookstore.microservice.order.dto.OrderDetailDTO;
import com.bookstore.microservice.order.exceptions.ResourceNotFoundException;
import com.bookstore.microservice.order.mappers.OrderMapper;
import com.bookstore.microservice.order.repository.OrderRepository;
import com.bookstore.microservice.order.services.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::toOrderDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        return OrderMapper.toOrderDTO(order);
    }

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setUserId(orderDTO.getUserId());
        order.setStatus("CREATED");

        for (OrderDetailDTO detailDTO : orderDTO.getOrderDetails()) {
            order.addItem(detailDTO.getBookId(), detailDTO.getQuantity(), detailDTO.getUnitPrice());
        }

        return OrderMapper.toOrderDTO(orderRepository.save(order));
    }

    @Override
    public OrderDTO updateOrder(Integer orderId, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!existingOrder.getStatus().equals(orderDTO.getStatus())) {
            existingOrder.setStatus(orderDTO.getStatus());
            existingOrder.markAsUpdated();
        }

        existingOrder.clearItems();
        for (OrderDetailDTO detailDTO : orderDTO.getOrderDetails()) {
            existingOrder.addItem(detailDTO.getBookId(), detailDTO.getQuantity(), detailDTO.getUnitPrice());
        }

        return OrderMapper.toOrderDTO(orderRepository.save(existingOrder));
    }

    @Override
    public List<OrderDetailDTO> getOrderDetails(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        return order.getOrderDetails().stream()
                .map(OrderMapper::toOrderDetailDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteOrder(Integer orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order not found with ID: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }
}
