package com.bookstore.microservice.inventory.services;

import com.david.bookstore.microservice.inventory.domain.Inventory;
import com.david.bookstore.microservice.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    public List<Inventory> getAllItems() {
        return inventoryRepository.getAllItems();
    }

    public Inventory getItemById(int bookId) {
        return inventoryRepository.getItemById(bookId);
    }

    public void addItem(Inventory inventory) {
        inventory.setCreatedDate(LocalDateTime.now());
        inventory.setUpdatedDate(LocalDateTime.now());
        inventoryRepository.createItem(inventory);
    }

    public void updateItem(int bookId, int quantity) {
        Inventory inventory = inventoryRepository.getItemById(bookId);
        if (inventory != null) {
            inventory.setQuantity(quantity);
            inventory.setUpdatedDate(LocalDateTime.now());
            inventoryRepository.updateItem(inventory);
        }
    }

    public void deleteItem(int bookId) {
        inventoryRepository.deleteItem(bookId);
    }
}
