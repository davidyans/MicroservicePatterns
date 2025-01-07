package com.bookstore.microservice.cart.dto;

import com.bookstore.microservice.cart.domain.CartDetail;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CartDTO {
    private Integer cartId;
    private Integer userId;
    private String status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private LocalDateTime expirationDate;
    private List<CartDetailDTO> details = new ArrayList<>();
}
