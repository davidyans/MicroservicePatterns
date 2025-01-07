package com.bookstore.microservice.inventory.mappers;

import com.bookstore.microservice.inventory.domain.Inventory;
import com.bookstore.microservice.inventory.dto.InventoryDTO;

public class InventoryMapper {

    public static InventoryDTO ToInventoryDTO(Inventory inventory) {

        if (inventory == null) {
            return null;
        }

        InventoryDTO dto = new InventoryDTO();
        dto.setBookId(inventory.getBookId());
        dto.setQuantity(inventory.getQuantity());
        dto.setCreatedDate(inventory.getCreatedDate());
        dto.setUpdatedDate(inventory.getUpdatedDate());
        return dto;
    }

    public static Inventory toInventoryEntity(InventoryDTO dto) {

        if (dto == null) {
            return null;
        }

        Inventory inventory = new Inventory();
        inventory.setBookId(dto.getBookId());
        inventory.setQuantity(dto.getQuantity());
        inventory.setCreatedDate(dto.getCreatedDate());
        inventory.setUpdatedDate(dto.getUpdatedDate());
        return inventory;
    }
}
