package com.bookstore.microservice.cart.repository;


import com.bookstore.microservice.cart.domain.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartDetailRepository extends JpaRepository<CartDetail, Integer> {
    List<CartDetail> findByCart_CartId(Integer cartId);
}
