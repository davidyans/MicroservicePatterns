package com.bookstore.saga_order_orchestator.events;

public class ReserveInventoryItem {
    private Integer bookId;
    private Integer quantityNeeded;

    public ReserveInventoryItem() {
    }

    public ReserveInventoryItem(Integer bookId, Integer quantityNeeded) {
        this.bookId = bookId;
        this.quantityNeeded = quantityNeeded;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getQuantityNeeded() {
        return quantityNeeded;
    }

    public void setQuantityNeeded(Integer quantityNeeded) {
        this.quantityNeeded = quantityNeeded;
    }
}
