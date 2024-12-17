package com.bookstore.microservice.cart.repository;

import com.bookstore.microservice.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Integer> {
}
