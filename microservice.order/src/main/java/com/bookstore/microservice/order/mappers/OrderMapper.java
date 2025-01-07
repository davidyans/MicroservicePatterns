package com.bookstore.microservice.order.mappers;

import com.bookstore.microservice.order.domain.Order;
import com.bookstore.microservice.order.domain.OrderDetail;
import com.bookstore.microservice.order.dto.OrderDTO;
import com.bookstore.microservice.order.dto.OrderDetailDTO;

import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderDTO toOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUserId());
        dto.setOrderDate(order.getOrderDate());
        dto.setTotal(order.getTotal());
        dto.setStatus(order.getStatus());
        dto.setUpdatedDate(order.getUpdatedDate());

        dto.setOrderDetails(order.getOrderDetails().stream()
                .map(OrderMapper::toOrderDetailDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    public static OrderDetailDTO toOrderDetailDTO(OrderDetail detail) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setBookId(detail.getId().getBookId());
        dto.setQuantity(detail.getQuantity());
        dto.setUnitPrice(detail.getUnitPrice());
        return dto;
    }
}
