package com.bookstore.microservice.inventory.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InventoryDTO {
    private Integer bookId;
    private Integer quantity;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
