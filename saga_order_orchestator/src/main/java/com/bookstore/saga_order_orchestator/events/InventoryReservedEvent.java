package com.bookstore.saga_order_orchestator.events;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservedEvent implements Serializable {
    private Integer orderId;
    private List<ReservedBookInfo> reservedBooks;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public List<ReservedBookInfo> getReservedBooks() {
        return reservedBooks;
    }

    public void setReservedBooks(List<ReservedBookInfo> reservedBooks) {
        this.reservedBooks = reservedBooks;
    }
}
