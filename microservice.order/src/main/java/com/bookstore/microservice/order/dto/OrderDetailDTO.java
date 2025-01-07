package com.bookstore.microservice.order.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderDetailDTO {
    private Integer bookId;
    private Integer quantity;
    private BigDecimal unitPrice;
}

