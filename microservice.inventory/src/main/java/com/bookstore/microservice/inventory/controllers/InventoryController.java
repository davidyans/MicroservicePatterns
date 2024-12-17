package com.bookstore.microservice.inventory.controllers;

import com.bookstore.microservice.inventory.dto.InventoryDTO;
import com.bookstore.microservice.inventory.services.InventoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryDTO>> getAllItems() {
        return ResponseEntity.ok(inventoryService.getAllItems());
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<InventoryDTO> getItemById(@PathVariable Integer bookId) {
        return ResponseEntity.ok(inventoryService.getItemById(bookId));
    }

    @PostMapping
    public ResponseEntity<InventoryDTO> addItem(@RequestBody InventoryDTO inventoryDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.addItem(inventoryDTO));
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<InventoryDTO> updateItem(@PathVariable Integer bookId, @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.updateItem(bookId, quantity));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Integer bookId) {
        inventoryService.deleteItem(bookId);
        return ResponseEntity.noContent().build();
    }
}
