package com.bookstore.microservice.cart.services;

import com.david.bookstore.microservice.cart.domain.Cart;
import com.david.bookstore.microservice.cart.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public List<Cart> getAllCarts() {
        return cartRepository.getAllCarts();
    }

    public Optional<Cart> getCartById(int cartId) {
        return Optional.ofNullable(cartRepository.getCartById(cartId));
    }

    public Cart createCart(Cart cart) {
        return cartRepository.createCart(cart);
    }

    public Cart updateCart(Cart cart) {
        return cartRepository.updateCart(cart);
    }

    public void deleteCart(int cartId) {
        cartRepository.deleteCart(cartId);
    }
}
