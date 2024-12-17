package com.bookstore.microservice.inventory.domain;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "inventory")
public class Inventory {
    private int bookId;
    private int quantity;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
