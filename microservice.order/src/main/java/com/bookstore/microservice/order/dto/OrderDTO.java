package com.bookstore.microservice.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Integer orderId;
    private Integer userId;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private String status;
    private LocalDateTime updatedDate;
    private List<OrderDetailDTO> orderDetails;
}
