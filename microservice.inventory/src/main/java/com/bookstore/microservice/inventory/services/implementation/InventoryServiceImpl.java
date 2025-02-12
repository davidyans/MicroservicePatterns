package com.bookstore.microservice.inventory.services.implementation;

import com.bookstore.microservice.inventory.clients.BookCatalogClient;
import com.bookstore.microservice.inventory.domain.Inventory;
import com.bookstore.microservice.inventory.dto.BookDTO;
import com.bookstore.microservice.inventory.dto.InventoryDTO;
import com.bookstore.microservice.inventory.exceptions.ResourceNotFoundException;
import com.bookstore.microservice.inventory.mappers.InventoryMapper;
import com.bookstore.microservice.inventory.repository.InventoryRepository;
import com.bookstore.microservice.inventory.services.InventoryService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    private static final String BOOKCATALOG_CB = "bookCatalogClient";

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private BookCatalogClient bookCatalogClient;

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
        validateBookExists(inventoryDTO.getBookId());

        Inventory inventory = InventoryMapper.toInventoryEntity(inventoryDTO);
        inventory.setCreatedDate(LocalDateTime.now());
        inventory.setUpdatedDate(LocalDateTime.now());
        return InventoryMapper.ToInventoryDTO(inventoryRepository.save(inventory));
    }

    @Override
    public InventoryDTO updateItem(Integer bookId, InventoryDTO inventoryDTO) {
        validateBookExists(bookId);

        Inventory inventory = inventoryRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found for book ID: " + bookId));
        inventory.setQuantity(inventoryDTO.getQuantity());
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

    @CircuitBreaker(name = BOOKCATALOG_CB, fallbackMethod = "bookFallbackSingle")
    @Retry(name = BOOKCATALOG_CB)
    private void validateBookExists(Integer bookId) {
        try {
            BookDTO book = bookCatalogClient.getBookById(bookId);
            if (book == null) {
                throw new ResourceNotFoundException("Book not found with ID: " + bookId);
            }
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Book not found with ID: " + bookId);
        } catch (FeignException e) {
            throw new RuntimeException("Error al comunicarse con Bookcatalog Service: " + e.getMessage());
        }
    }

    private void bookFallbackSingle(Integer bookId, Throwable throwable) {
        throw new RuntimeException("Bookcatalog Service está inactivo. Por favor, intenta más tarde.");
    }

    @CircuitBreaker(name = BOOKCATALOG_CB, fallbackMethod = "bookFallbackList")
    @Retry(name = BOOKCATALOG_CB)
    private void validateBooksExist(List<InventoryDTO> cartDetails) {
        List<Integer> bookIds = cartDetails.stream()
                .map(InventoryDTO::getBookId)
                .toList();

        for (Integer bookId : bookIds) {
            validateBookExists(bookId);
        }
    }

    private void bookFallbackList(List<InventoryDTO> cartDetails, Throwable throwable) {
        throw new RuntimeException("Bookcatalog Service está inactivo. Por favor, intenta más tarde.");
    }
}
