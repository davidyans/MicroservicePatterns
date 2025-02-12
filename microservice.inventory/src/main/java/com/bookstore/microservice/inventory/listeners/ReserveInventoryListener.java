package com.bookstore.microservice.inventory.listeners;

import com.bookstore.microservice.inventory.config.RabbitMQConfig;
import com.bookstore.microservice.inventory.domain.Inventory;
import com.bookstore.microservice.inventory.events.*;
import com.bookstore.microservice.inventory.repository.InventoryRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReserveInventoryListener {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.INVENTORY_RESERVE_QUEUE)
    public void handleReserveInventoryCommand(ReserveInventoryCommand command) {
        System.out.println(">>> [Inventory] Received ReserveInventoryCommand for OrderID: "
                + command.getOrderId());

        List<ReserveInventoryItem> itemsToReserve = command.getItems();

        for (ReserveInventoryItem item : itemsToReserve) {
            Optional<Inventory> invOpt = inventoryRepository.findById(item.getBookId());
            if (invOpt.isEmpty()) {
                publishFailedEvent(command, "Book not found in inventory", itemsToReserve);
                return;
            }
            Inventory inv = invOpt.get();
            if (inv.getQuantity() < item.getQuantityNeeded()) {
                String reason = String.format(
                        "Not enough stock for bookId=%d. Requested=%d, Available=%d",
                        item.getBookId(), item.getQuantityNeeded(), inv.getQuantity()
                );
                publishFailedEvent(command, reason, itemsToReserve);
                return;
            }
        }

        List<ReservedBookInfo> reservedBooks = new ArrayList<>();
        for (ReserveInventoryItem item : itemsToReserve) {
            Inventory inv = inventoryRepository.findById(item.getBookId()).get();

            int newQuantity = inv.getQuantity() - item.getQuantityNeeded();
            inv.setQuantity(newQuantity);
            inv.setUpdatedDate(LocalDateTime.now());
            inventoryRepository.save(inv);

            if (newQuantity == 0) {
                publishStockStatusUpdated(inv.getBookId(), 0);
            }

            reservedBooks.add(new ReservedBookInfo(item.getBookId(), item.getQuantityNeeded()));
        }

        InventoryReservedEvent successEvent = new InventoryReservedEvent(
                command.getOrderId(),
                reservedBooks
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.INVENTORY_EXCHANGE,
                RabbitMQConfig.INVENTORY_RESERVED_RK,
                successEvent
        );

        System.out.println(">>> [Inventory] InventoryReservedEvent published: " + successEvent);
    }

    private void publishFailedEvent(ReserveInventoryCommand command, String reason,
                                    List<ReserveInventoryItem> failedItems) {
        InventoryReservationFailedEvent failedEvent = new InventoryReservationFailedEvent(
                command.getOrderId(),
                reason,
                failedItems
        );
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.INVENTORY_EXCHANGE,
                RabbitMQConfig.INVENTORY_RESERVE_FAILED_RK,
                failedEvent
        );

        System.out.println(">>> [Inventory] InventoryReservationFailedEvent published: " + failedEvent);
    }

    private void publishStockStatusUpdated(Integer bookId, int newStatus) {
        StockStatusUpdatedEvent event = new StockStatusUpdatedEvent(bookId, newStatus);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BOOK_CATALOG_EXCHANGE,
                RabbitMQConfig.BOOK_STOCK_UPDATE_RK,
                event
        );
        System.out.println(">>> [Inventory] StockStatusUpdatedEvent published: " + event);
    }
}
