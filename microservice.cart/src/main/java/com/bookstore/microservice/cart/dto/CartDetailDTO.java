package com.bookstore.microservice.cart.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CartDetailDTO {
    private Integer cartDetailId;
    private Integer bookId;
    private Integer quantity;
    private Double unitPrice;
    private LocalDateTime addedDate;
    private LocalDateTime updatedDate;
}
