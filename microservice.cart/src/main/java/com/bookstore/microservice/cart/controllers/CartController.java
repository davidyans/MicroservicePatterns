package com.bookstore.microservice.cart.controllers;

import com.bookstore.microservice.cart.dto.CartDetailDTO;
import com.bookstore.microservice.cart.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{id}/items")
    public ResponseEntity<List<CartDetailDTO>> getCartItems(@PathVariable Integer id) {
        return ResponseEntity.ok(cartService.getCartItems(id));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<CartDetailDTO> addItemToCart(@PathVariable Integer id, @RequestBody CartDetailDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addItemToCart(id, dto));
    }

    @PutMapping("/{id}/items/{itemId}")
    public ResponseEntity<CartDetailDTO> updateCartItem(@PathVariable Integer id, @PathVariable Integer itemId, @RequestBody CartDetailDTO dto) {
        return ResponseEntity.ok(cartService.updateCartItem(id, itemId, dto));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Integer id, @PathVariable Integer itemId) {
        cartService.deleteCartItem(id, itemId);
        return ResponseEntity.noContent().build();
    }
}
