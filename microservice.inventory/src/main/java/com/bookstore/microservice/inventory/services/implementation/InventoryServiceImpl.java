package com.bookstore.microservice.inventory.services.implementation;

import com.bookstore.microservice.inventory.domain.Inventory;
import com.bookstore.microservice.inventory.dto.InventoryDTO;
import com.bookstore.microservice.inventory.exceptions.ResourceNotFoundException;
import com.bookstore.microservice.inventory.mappers.InventoryMapper;
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
                .map(InventoryMapper::ToInventoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryDTO getItemById(Integer bookId) {
        Inventory inventory = inventoryRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found for book ID: " + bookId));
        return InventoryMapper.ToInventoryDTO(inventory);
    }

    @Override
    public InventoryDTO addItem(InventoryDTO inventoryDTO) {
        Inventory inventory = InventoryMapper.toInventoryEntity(inventoryDTO);
        inventory.setCreatedDate(LocalDateTime.now());
        inventory.setUpdatedDate(LocalDateTime.now());
        return InventoryMapper.ToInventoryDTO(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryDTO updateItem(Integer bookId, Integer quantity) {
        Inventory inventory = inventoryRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found for book ID: " + bookId));
        inventory.setQuantity(quantity);
        inventory.setUpdatedDate(LocalDateTime.now());
        return InventoryMapper.ToInventoryDTO(inventoryRepository.save(inventory));
    }

    @Override
    public void deleteItem(Integer bookId) {
        if (!inventoryRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Inventory item not found for book ID: " + bookId);
        }
        inventoryRepository.deleteById(bookId);
    }
}
