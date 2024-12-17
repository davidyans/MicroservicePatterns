package com.bookstore.microservice.inventory.repository;

import com.david.bookstore.microservice.inventory.domain.Inventory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class InventoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public InventoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static Inventory mapRow(ResultSet rs, int rowNum) throws SQLException {
        Inventory inventory = new Inventory();
        inventory.setBookId(rs.getInt("book_id"));
        inventory.setQuantity(rs.getInt("quantity"));
        inventory.setCreatedDate(rs.getObject("created_date", LocalDateTime.class));
        inventory.setUpdatedDate(rs.getObject("updated_date", LocalDateTime.class));
        return inventory;
    }

    public List<Inventory> getAllItems() {
        String sql = "SELECT * FROM inventory";
        return jdbcTemplate.query(sql, InventoryRepository::mapRow);
    }

    public Inventory getItemById(int bookId) {
        String sql = "SELECT * FROM inventory WHERE book_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{bookId}, InventoryRepository::mapRow);
    }

    public void createItem(Inventory inventory) {
        String sql = "INSERT INTO inventory (book_id, quantity, created_date, updated_date) " +
                "VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, inventory.getBookId(), inventory.getQuantity(),
                inventory.getCreatedDate(), inventory.getUpdatedDate());
    }

    public void updateItem(Inventory inventory) {
        String sql = "UPDATE inventory SET quantity = ?, updated_date = ? WHERE book_id = ?";
        jdbcTemplate.update(sql, inventory.getQuantity(), inventory.getUpdatedDate(), inventory.getBookId());
    }

    public void deleteItem(int bookId) {
        String sql = "DELETE FROM inventory WHERE book_id = ?";
        jdbcTemplate.update(sql, bookId);
    }
}
