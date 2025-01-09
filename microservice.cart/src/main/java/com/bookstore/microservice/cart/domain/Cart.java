package com.bookstore.microservice.cart.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Integer cartId;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartDetail> details = new ArrayList<>();

    public void addItem(CartDetail item) {
        this.details.add(item);
        item.setCart(this);
        markAsUpdated();
    }

    public void updateItem(Integer itemId, Integer quantity, Double unitPrice) {
        CartDetail detail = this.details.stream()
                .filter(d -> d.getCartDetailId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        detail.setQuantity(quantity);
        detail.setUnitPrice(unitPrice);
        detail.setUpdatedDate(LocalDateTime.now());
        markAsUpdated();
    }

    public void removeItem(Integer itemId) {
        this.details.removeIf(d -> d.getCartDetailId().equals(itemId));
    }

    public void markAsUpdated() {
        this.updatedDate = LocalDateTime.now();
    }
}
