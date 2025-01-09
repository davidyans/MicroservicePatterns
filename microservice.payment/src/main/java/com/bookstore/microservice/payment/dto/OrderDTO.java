package com.bookstore.microservice.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Integer orderId;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private String status;
    private LocalDateTime updatedDate;
    private List<OrderDetailDTO> orderDetails;
}
