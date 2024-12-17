package com.bookstore.microservice.inventory.services.implementation;

import com.bookstore.microservice.inventory.domain.Inventory;
import com.bookstore.microservice.inventory.dto.InventoryDTO;
import com.bookstore.microservice.inventory.exceptions.ResourceNotFoundException;
import com.bookstore.microservice.inventory.repository.InventoryRepository;
import com.bookstore.microservice.inventory.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public List<InventoryDTO> getAllItems() {
        return inventoryRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryDTO getItemById(Integer bookId) {
        Inventory inventory = inventoryRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found for book ID: " + bookId));
        return convertToDTO(inventory);
    }

    @Override
    public InventoryDTO addItem(InventoryDTO inventoryDTO) {
        Inventory inventory = convertToEntity(inventoryDTO);
        inventory.setCreatedDate(LocalDateTime.now());
        inventory.setUpdatedDate(LocalDateTime.now());
        return convertToDTO(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryDTO updateItem(Integer bookId, Integer quantity) {
        Inventory inventory = inventoryRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found for book ID: " + bookId));
        inventory.setQuantity(quantity);
        inventory.setUpdatedDate(LocalDateTime.now());
        return convertToDTO(inventoryRepository.save(inventory));
    }

    @Override
    public void deleteItem(Integer bookId) {
        if (!inventoryRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Inventory item not found for book ID: " + bookId);
        }
        inventoryRepository.deleteById(bookId);
    }

    private InventoryDTO convertToDTO(Inventory inventory) {
        InventoryDTO dto = new InventoryDTO();
        dto.setBookId(inventory.getBookId());
        dto.setQuantity(inventory.getQuantity());
        dto.setCreatedDate(inventory.getCreatedDate());
        dto.setUpdatedDate(inventory.getUpdatedDate());
        return dto;
    }

    private Inventory convertToEntity(InventoryDTO dto) {
        Inventory inventory = new Inventory();
        inventory.setBookId(dto.getBookId());
        inventory.setQuantity(dto.getQuantity());
        inventory.setCreatedDate(dto.getCreatedDate());
        inventory.setUpdatedDate(dto.getUpdatedDate());
        return inventory;
    }
}
