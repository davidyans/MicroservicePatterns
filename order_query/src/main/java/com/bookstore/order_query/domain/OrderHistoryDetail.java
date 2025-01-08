package com.bookstore.order_query.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "order_history_detail")
public class OrderHistoryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_history_detail_id")
    private Long orderHistoryDetailId;

    @ManyToOne
    @JoinColumn(name = "order_history_id")
    private OrderHistory orderHistory;

    @Column(name = "book_id", nullable = false)
    private Integer bookId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    public Long getOrderHistoryDetailId() {
        return orderHistoryDetailId;
    }

    public void setOrderHistoryDetailId(Long orderHistoryDetailId) {
        this.orderHistoryDetailId = orderHistoryDetailId;
    }

    public OrderHistory getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(OrderHistory orderHistory) {
        this.orderHistory = orderHistory;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
