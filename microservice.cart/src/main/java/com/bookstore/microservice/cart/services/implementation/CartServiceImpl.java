package com.bookstore.microservice.cart.services.implementation;

import com.bookstore.microservice.cart.domain.Cart;
import com.bookstore.microservice.cart.domain.CartDetail;
import com.bookstore.microservice.cart.dto.CartDTO;
import com.bookstore.microservice.cart.dto.CartDetailDTO;
import com.bookstore.microservice.cart.exceptions.ResourceNotFoundException;
import com.bookstore.microservice.cart.repository.CartDetailRepository;
import com.bookstore.microservice.cart.repository.CartRepository;

import com.bookstore.microservice.cart.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Override
    public List<CartDTO> getAllCarts() {
        return cartRepository.findAll().stream()
                .map(this::convertCartToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CartDTO getCartById(Integer cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));
        return convertCartToDTO(cart);
    }

    @Override
    public CartDTO createCart(CartDTO cartDTO) {
        Cart cart = convertCartToEntity(cartDTO);
        cart.setCreatedDate(LocalDateTime.now());
        return convertCartToDTO(cartRepository.save(cart));
    }

    @Override
    public CartDTO updateCart(Integer cartId, CartDTO cartDTO) {
        Cart existingCart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));

        existingCart.setUserId(cartDTO.getUserId());
        existingCart.setStatus(cartDTO.getStatus());
        existingCart.setExpirationDate(cartDTO.getExpirationDate());
        existingCart.setUpdatedDate(LocalDateTime.now());

        return convertCartToDTO(cartRepository.save(existingCart));
    }

    @Override
    public void deleteCart(Integer cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new ResourceNotFoundException("Cart not found with ID: " + cartId);
        }
        cartRepository.deleteById(cartId);
    }

    @Override
    public List<CartDetailDTO> getCartItems(Integer cartId) {
        return cartDetailRepository.findByCart_CartId(cartId)
                .stream()
                .map(this::convertCartDetailToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CartDetailDTO addItemToCart(Integer cartId, CartDetailDTO dto) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartDetail detail = new CartDetail();
        detail.setCart(cart);
        detail.setBookId(dto.getBookId());
        detail.setQuantity(dto.getQuantity());
        detail.setUnitPrice(dto.getUnitPrice());
        detail.setAddedDate(LocalDateTime.now());

        return convertCartDetailToDTO(cartDetailRepository.save(detail));
    }

    @Override
    public CartDetailDTO updateCartItem(Integer cartId, Integer cartDetailId, CartDetailDTO dto) {
        CartDetail detail = cartDetailRepository.findById(cartDetailId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart detail not found"));

        detail.setQuantity(dto.getQuantity());
        detail.setUnitPrice(dto.getUnitPrice());
        detail.setUpdatedDate(LocalDateTime.now());

        return convertCartDetailToDTO(cartDetailRepository.save(detail));
    }

    @Override
    public void deleteCartItem(Integer cartId, Integer cartDetailId) {
        CartDetail detail = cartDetailRepository.findById(cartDetailId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart detail not found"));
        cartDetailRepository.delete(detail);
    }

    private CartDetailDTO convertCartDetailToDTO(CartDetail detail) {
        CartDetailDTO dto = new CartDetailDTO();
        dto.setCartDetailId(detail.getCartDetailId());
        dto.setBookId(detail.getBookId());
        dto.setQuantity(detail.getQuantity());
        dto.setUnitPrice(detail.getUnitPrice());
        dto.setAddedDate(detail.getAddedDate());
        return dto;
    }

    private CartDTO convertCartToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setCartId(cart.getCartId());
        dto.setUserId(cart.getUserId());
        dto.setStatus(cart.getStatus());
        dto.setCreatedDate(cart.getCreatedDate());
        dto.setExpirationDate(cart.getExpirationDate());
        return dto;
    }

    private Cart convertCartToEntity(CartDTO dto) {
        Cart cart = new Cart();
        cart.setUserId(dto.getUserId());
        cart.setStatus(dto.getStatus());
        cart.setExpirationDate(dto.getExpirationDate());
        return cart;
    }
}
