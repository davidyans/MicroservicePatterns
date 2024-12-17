package com.bookstore.microservice.inventory.controllers;

import com.david.bookstore.microservice.inventory.domain.Inventory;
import com.david.bookstore.microservice.inventory.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("")
    public ResponseEntity<List<Inventory>> getAllItems() {
        return ResponseEntity.ok(inventoryService.getAllItems());
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<Inventory> getItemById(@PathVariable int bookId) {
        return ResponseEntity.ok(inventoryService.getItemById(bookId));
    }

    @PostMapping("")
    public ResponseEntity<String> addItem(@RequestBody Inventory inventory) {
        inventoryService.addItem(inventory);
        return ResponseEntity.ok("Item added successfully");
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<String> updateItem(@PathVariable int bookId, @RequestParam int quantity) {
        inventoryService.updateItem(bookId, quantity);
        return ResponseEntity.ok("Item updated successfully");
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<String> deleteItem(@PathVariable int bookId) {
        inventoryService.deleteItem(bookId);
        return ResponseEntity.ok("Item deleted successfully");
    }
}
