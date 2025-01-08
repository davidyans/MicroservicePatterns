package com.bookstore.order_command.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
public class OrderDetailId implements Serializable {
    private Integer orderId;
    private Integer bookId;

    public OrderDetailId() {
    }

    public OrderDetailId(Integer orderId, Integer bookId) {
        this.orderId = orderId;
        this.bookId = bookId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }
}
