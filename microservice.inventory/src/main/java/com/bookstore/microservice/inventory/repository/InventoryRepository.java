package com.bookstore.microservice.inventory.repository;

import com.bookstore.microservice.inventory.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
}
