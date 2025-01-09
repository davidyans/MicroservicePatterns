package com.bookstore.microservice.cart.mappers;

import com.bookstore.microservice.cart.domain.Cart;
import com.bookstore.microservice.cart.domain.CartDetail;
import com.bookstore.microservice.cart.dto.CartDTO;
import com.bookstore.microservice.cart.dto.CartDetailDTO;
import java.util.List;
import java.util.stream.Collectors;

public class CartMapper {

    public static CartDTO toCartDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setCartId(cart.getCartId());
        dto.setStatus(cart.getStatus());
        dto.setCreatedDate(cart.getCreatedDate());
        dto.setUpdatedDate(cart.getUpdatedDate());
        dto.setExpirationDate(cart.getExpirationDate());
        dto.setDetails(cart.getDetails().stream()
                .map(CartMapper::toCartDetailDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    public static Cart toCartEntity(CartDTO dto) {
        Cart cart = new Cart();
        cart.setStatus(dto.getStatus());
        cart.setExpirationDate(dto.getExpirationDate());
        return cart;
    }

    public static CartDetailDTO toCartDetailDTO(CartDetail detail) {
        CartDetailDTO dto = new CartDetailDTO();
        dto.setCartDetailId(detail.getCartDetailId());
        dto.setBookId(detail.getBookId());
        dto.setQuantity(detail.getQuantity());
        dto.setUnitPrice(detail.getUnitPrice());
        dto.setAddedDate(detail.getAddedDate());
        return dto;
    }

    public static CartDetail toCartDetailEntity(CartDetailDTO dto) {
        CartDetail detail = new CartDetail();
        detail.setBookId(dto.getBookId());
        detail.setQuantity(dto.getQuantity());
        detail.setUnitPrice(dto.getUnitPrice());
        return detail;
    }
}
