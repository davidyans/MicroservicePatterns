package com.bookstore.microservice.cart.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CartDTO {
    private Integer cartId;
    private Integer userId;
    private String status;
    private LocalDateTime createdDate;
    private LocalDateTime expirationDate;
}
