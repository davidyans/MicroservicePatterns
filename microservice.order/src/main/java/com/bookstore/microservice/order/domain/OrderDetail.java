package com.bookstore.microservice.order.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@Table(name = "order_detail")
public class OrderDetail {

    @EmbeddedId
    private OrderDetailId id;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    public OrderDetail(OrderDetailId id, Order order, Integer quantity, BigDecimal unitPrice) {
        this.id = id;
        this.order = order;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public OrderDetail() {

    }
}
