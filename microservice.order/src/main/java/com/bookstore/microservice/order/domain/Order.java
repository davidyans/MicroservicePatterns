package com.bookstore.microservice.order.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(name = "total", nullable = false)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public void addItem(Integer bookId, Integer quantity, BigDecimal unitPrice) {
        OrderDetail detail = new OrderDetail(new OrderDetailId(this.orderId, bookId), this, quantity, unitPrice);
        this.orderDetails.add(detail);
        updateTotal();
    }

    public void clearItems() {
        this.orderDetails.clear();
        updateTotal();
    }

    private void updateTotal() {
        this.total = orderDetails.stream()
                .map(detail -> detail.getUnitPrice().multiply(BigDecimal.valueOf(detail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void markAsUpdated() {
        this.updatedDate = LocalDateTime.now();
    }
}
