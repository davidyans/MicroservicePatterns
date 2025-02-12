package com.bookstore.microservice.cart.services.implementation;

import com.bookstore.microservice.cart.clients.BookCatalogClient;
import com.bookstore.microservice.cart.domain.Cart;
import com.bookstore.microservice.cart.domain.CartDetail;
import com.bookstore.microservice.cart.dto.BookDTO;
import com.bookstore.microservice.cart.dto.CartDTO;
import com.bookstore.microservice.cart.dto.CartDetailDTO;
import com.bookstore.microservice.cart.exceptions.ResourceNotFoundException;
import com.bookstore.microservice.cart.mappers.CartMapper;
import com.bookstore.microservice.cart.repository.CartDetailRepository;
import com.bookstore.microservice.cart.repository.CartRepository;

import com.bookstore.microservice.cart.services.CartService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private static final String BOOKCATALOG_CB = "bookCatalogClient";

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private BookCatalogClient bookCatalogClient;

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
        validateBooksExist(cartDTO.getDetails());
        Cart cart = CartMapper.toCartEntity(cartDTO);

        return CartMapper.toCartDTO(cartRepository.save(cart));
    }

    @Override
    public CartDTO updateCart(Integer cartId, CartDTO cartDTO) {
        validateBooksExist(cartDTO.getDetails());
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
        validateBookExists(dto.getBookId());

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with ID: " + cartId));

        CartDetail existingDetail = cart.getDetails()
                .stream()
                .filter(detail -> detail.getBookId().equals(dto.getBookId()))
                .findFirst()
                .orElse(null);

        if (existingDetail != null) {

            existingDetail.setQuantity(existingDetail.getQuantity() + dto.getQuantity());
            existingDetail.setUnitPrice(dto.getUnitPrice());
        } else {
            CartDetail newDetail = CartMapper.toCartDetailEntity(dto);
            cart.addItem(newDetail);
        }

        cartRepository.save(cart);

        return CartMapper.toCartDetailDTO(
                existingDetail != null ? existingDetail : cart.getDetails()
                        .stream()
                        .filter(detail -> detail.getBookId().equals(dto.getBookId()))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Error al agregar el ítem al carrito"))
        );
    }

    @Override
    public CartDetailDTO updateCartItem(Integer cartId, Integer cartDetailId, CartDetailDTO dto) {
        validateBookExists(dto.getBookId());
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

    @Override
    public void deleteAllCartItems(Integer cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found with id: " + cartId));

        cart.getDetails().clear();
        cartRepository.save(cart);
    }

    @CircuitBreaker(name = BOOKCATALOG_CB, fallbackMethod = "bookFallbackSingle")
    @Retry(name = BOOKCATALOG_CB)
    private void validateBookExists(Integer bookId) {
        try {
            BookDTO book = bookCatalogClient.getBookById(bookId);
            if (book == null) {
                throw new ResourceNotFoundException("Book not found with ID: " + bookId);
            }
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Book not found with ID: " + bookId);
        } catch (FeignException e) {
            throw new RuntimeException("Error al comunicarse con Bookcatalog Service: " + e.getMessage());
        }
    }

    private void bookFallbackSingle(Integer bookId, Throwable throwable) {
        throw new RuntimeException("Bookcatalog Service está inactivo. Por favor, intenta más tarde.");
    }

    @CircuitBreaker(name = BOOKCATALOG_CB, fallbackMethod = "bookFallbackList")
    @Retry(name = BOOKCATALOG_CB)
    private void validateBooksExist(List<CartDetailDTO> cartDetails) {
        List<Integer> bookIds = cartDetails.stream()
                .map(CartDetailDTO::getBookId)
                .toList();

        for (Integer bookId : bookIds) {
            validateBookExists(bookId);
        }
    }

    private void bookFallbackList(List<CartDetailDTO> cartDetails, Throwable throwable) {
        throw new RuntimeException("Bookcatalog Service está inactivo. Por favor, intenta más tarde.");
    }
}
