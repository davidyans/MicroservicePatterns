package com.bookstore.microservice.inventory.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservedEvent implements Serializable {
    private Integer orderId;
    private List<ReservedBookInfo> reservedBooks;
}
