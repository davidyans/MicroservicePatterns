package com.bookstore.microservice.cart.services.implementation;

import com.bookstore.microservice.cart.domain.Cart;
import com.bookstore.microservice.cart.domain.CartDetail;
import com.bookstore.microservice.cart.dto.CartDTO;
import com.bookstore.microservice.cart.dto.CartDetailDTO;
import com.bookstore.microservice.cart.exceptions.ResourceNotFoundException;
import com.bookstore.microservice.cart.mappers.CartMapper;
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
                .map(CartMapper::toCartDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CartDTO getCartById(Integer cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));
        return CartMapper.toCartDTO(cart);
    }

    @Override
    public CartDTO createCart(CartDTO cartDTO) {
        Cart cart = CartMapper.toCartEntity(cartDTO);
        return CartMapper.toCartDTO(cartRepository.save(cart));
    }

    @Override
    public CartDTO updateCart(Integer cartId, CartDTO cartDTO) {
        Cart existingCart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));

        if (!existingCart.getStatus().equals(cartDTO.getStatus())) {
            existingCart.setStatus(cartDTO.getStatus());
            existingCart.markAsUpdated();
        }

        existingCart.setExpirationDate(cartDTO.getExpirationDate());

        return CartMapper.toCartDTO(cartRepository.save(existingCart));
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
                .map(CartMapper::toCartDetailDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CartDetailDTO addItemToCart(Integer cartId, CartDetailDTO dto) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartDetail detail = CartMapper.toCartDetailEntity(dto);
        cart.addItem(detail);
        cartRepository.save(cart);

        return CartMapper.toCartDetailDTO(detail);
    }

    @Override
    public CartDetailDTO updateCartItem(Integer cartId, Integer cartDetailId, CartDetailDTO dto) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));

        cart.updateItem(cartDetailId, dto.getQuantity(), dto.getUnitPrice());

        return CartMapper.toCartDetailDTO(cartRepository.save(cart)
                .getDetails()
                .stream()
                .filter(d -> d.getCartDetailId().equals(cartDetailId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart detail not found")));
    }

    @Override
    public void deleteCartItem(Integer cartId, Integer cartDetailId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));

        cart.removeItem(cartDetailId);
        cart.markAsUpdated();

        cartRepository.save(cart);
    }
}
