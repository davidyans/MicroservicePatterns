package com.bookstore.microservice.inventory.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockStatusUpdatedEvent implements Serializable {
    private Integer bookId;
    private Integer newStockStatus; // 0 = agotado, 1 = en stock
}
