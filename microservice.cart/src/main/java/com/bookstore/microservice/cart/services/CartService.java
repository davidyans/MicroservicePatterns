package com.bookstore.microservice.cart.services;

import com.bookstore.microservice.cart.dto.CartDTO;
import com.bookstore.microservice.cart.dto.CartDetailDTO;

import java.util.List;

public interface CartService {
    List<CartDTO> getAllCarts();
    CartDTO getCartById(Integer cartId);
    CartDTO createCart(CartDTO cartDTO);
    CartDTO updateCart(Integer cartId, CartDTO cartDTO);
    void deleteCart(Integer cartId);

    List<CartDetailDTO> getCartItems(Integer cartId);
    CartDetailDTO addItemToCart(Integer cartId, CartDetailDTO cartDetailDTO);
    CartDetailDTO updateCartItem(Integer cartId, Integer cartDetailId, CartDetailDTO cartDetailDTO);
    void deleteCartItem(Integer cartId, Integer cartDetailId);
}
