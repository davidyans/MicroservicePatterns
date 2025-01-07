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

        // 1) Validar stock para TODOS los libros primero
        for (ReserveInventoryItem item : itemsToReserve) {
            Optional<Inventory> invOpt = inventoryRepository.findById(item.getBookId());
            if (invOpt.isEmpty()) {
                // No existe el libro en inventario => Fallo y salimos
                publishFailedEvent(command, "Book not found in inventory", itemsToReserve);
                return;
            }
            Inventory inv = invOpt.get();
            if (inv.getQuantity() < item.getQuantityNeeded()) {
                // No hay suficiente stock => Fallo y salimos
                String reason = String.format(
                        "Not enough stock for bookId=%d. Requested=%d, Available=%d",
                        item.getBookId(), item.getQuantityNeeded(), inv.getQuantity()
                );
                publishFailedEvent(command, reason, itemsToReserve);
                return;
            }
        }

        // 2) Si llegamos aquí, significa que TODOS tienen stock suficiente.
        // Procedemos a deducir el stock de cada libro
        List<ReservedBookInfo> reservedBooks = new ArrayList<>();
        for (ReserveInventoryItem item : itemsToReserve) {
            Inventory inv = inventoryRepository.findById(item.getBookId()).get();

            int newQuantity = inv.getQuantity() - item.getQuantityNeeded();
            inv.setQuantity(newQuantity);
            inv.setUpdatedDate(LocalDateTime.now());
            inventoryRepository.save(inv);

            // Aquí es donde publicas el evento de actualización de stock
            // si la cantidad llega a cero (o cambia de 0 a > 0, etc.)
            if (newQuantity == 0) {
                publishStockStatusUpdated(inv.getBookId(), 0);
            }
            // Si deseas manejar también la reposición (por ejemplo, si pasa de 0 a >0),
            // lo harías análogamente en otro método/endpoint.

            // Construir la lista de reservas efectivas
            reservedBooks.add(new ReservedBookInfo(item.getBookId(), item.getQuantityNeeded()));
        }

        // 3) Publicar evento de éxito con la lista de todos los libros reservados
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
