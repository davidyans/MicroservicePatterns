package com.bookstore.microservice.cart.repository;

import com.david.bookstore.microservice.cart.domain.Cart;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CartRepository {

    private final JdbcTemplate jdbcTemplate;

    public CartRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static Cart mapRow(ResultSet rs, int rowNum) throws SQLException {
        Cart cart = new Cart();
        cart.setCartId(rs.getInt("cart_id"));
        cart.setUserId(rs.getInt("user_id"));
        cart.setCreatedDate(rs.getObject("creation_date", LocalDateTime.class));
        cart.setUpdatedDate(rs.getObject("updated_date", LocalDateTime.class));
        cart.setStatus(rs.getString("status"));
        cart.setExpirationDate(rs.getObject("expiration_date", LocalDateTime.class));
        return cart;
    }

    public List<Cart> getAllCarts() {
        String sql = "SELECT * FROM cart";
        return jdbcTemplate.query(sql, CartRepository::mapRow);
    }

    public Cart getCartById(int cartId) {
        String sql = "SELECT * FROM cart WHERE cart_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{cartId}, CartRepository::mapRow);
    }

    public Cart createCart(Cart cart) {
        String sql = "INSERT INTO cart (user_id, creation_date, updated_date, status, expiration_date) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, cart.getUserId(), cart.getCreatedDate(), cart.getUpdatedDate(),
                cart.getStatus(), cart.getExpirationDate());
        return cart;
    }

    public Cart updateCart(Cart cart) {
        String sql = "UPDATE cart SET updated_date = ?, status = ?, expiration_date = ? WHERE cart_id = ?";
        jdbcTemplate.update(sql, cart.getUpdatedDate(), cart.getStatus(), cart.getExpirationDate(), cart.getCartId());
        return cart;
    }

    public void deleteCart(int cartId) {
        String sql = "DELETE FROM cart WHERE cart_id = ?";
        jdbcTemplate.update(sql, cartId);
    }
}
