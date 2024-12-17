package com.bookstore.microservice.inventory.services;

import com.bookstore.microservice.inventory.dto.InventoryDTO;

import java.util.List;

public interface InventoryService {
    List<InventoryDTO> getAllItems();
    InventoryDTO getItemById(Integer bookId);
    InventoryDTO addItem(InventoryDTO inventoryDTO);
    InventoryDTO updateItem(Integer bookId, Integer quantity);
    void deleteItem(Integer bookId);
}
