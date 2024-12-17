package com.bookstore.microservice.order.repository;

import com.david.bookstore.microservice.order.domain.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setUserId(rs.getInt("user_id"));
        order.setOrderDate(rs.getObject("order_date", LocalDateTime.class));
        order.setTotal(rs.getBigDecimal("total"));
        order.setStatus(rs.getString("status"));
        order.setUpdatedDate(rs.getObject("updated_date", LocalDateTime.class));
        return order;
    }

    public List<Order> getAllOrders() {
        String sql = "SELECT * FROM orders";
        return jdbcTemplate.query(sql, OrderRepository::mapRow);
    }

    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{orderId}, OrderRepository::mapRow);
    }

    public Order createOrder(Order order) {
        String sql = "INSERT INTO orders (user_id, order_date, total, status, updated_date) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, order.getUserId(), order.getOrderDate(), order.getTotal(),
                order.getStatus(), order.getUpdatedDate());
        return order;
    }

    public Order updateOrder(Order order) {
        String sql = "UPDATE orders SET total = ?, status = ?, updated_date = ? WHERE order_id = ?";
        jdbcTemplate.update(sql, order.getTotal(), order.getStatus(), order.getUpdatedDate(), order.getOrderId());
        return order;
    }

    public void deleteOrder(int orderId) {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        jdbcTemplate.update(sql, orderId);
    }
}
