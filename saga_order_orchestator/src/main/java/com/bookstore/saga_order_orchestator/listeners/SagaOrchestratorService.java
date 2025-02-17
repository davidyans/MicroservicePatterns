package com.bookstore.saga_order_orchestator.listeners;

import com.bookstore.saga_order_orchestator.config.RabbitMQConfig;
import com.bookstore.saga_order_orchestator.events.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SagaOrchestratorService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final Map<Integer, List<ReserveInventoryItem>> orderItemsCache = new ConcurrentHashMap<>();

    @RabbitListener(queues = RabbitMQConfig.ORCH_ORDER_CREATED_QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.println(">>> [Orchestrator] OrderCreatedEvent received for OrderID: " + event.getOrderId());

        List<ReserveInventoryItem> items = event.getOrderDetails().stream()
                .map(detail -> new ReserveInventoryItem(detail.getBookId(), detail.getQuantity()))
                .toList();

        orderItemsCache.put(event.getOrderId(), items);

        ProcessPaymentCommand cmd = new ProcessPaymentCommand(
                event.getOrderId(),
                event.getTotal(),
                "CREDIT_CARD"
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_PROCESS_RK,
                cmd
        );

        System.out.println(">>> [Orchestrator] ProcessPaymentCommand sent for OrderID: " + event.getOrderId());
    }

    @RabbitListener(queues = RabbitMQConfig.ORCH_PAYMENT_COMPLETED_QUEUE)
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        System.out.println(">>> [Orchestrator] PaymentCompletedEvent received for OrderID: " + event.getOrderId());

        List<ReserveInventoryItem> items = orderItemsCache.get(event.getOrderId());

        if (items == null) {
            System.err.println("No items found for orderId: " + event.getOrderId());
            return;
        }

        ReserveInventoryCommand cmd = new ReserveInventoryCommand(event.getOrderId(), items);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.INVENTORY_EXCHANGE,
                RabbitMQConfig.INVENTORY_RESERVE_RK,
                cmd
        );

        System.out.println(">>> [Orchestrator] ReserveInventoryCommand sent for OrderID: " + event.getOrderId());
    }

    @RabbitListener(queues = RabbitMQConfig.ORCH_PAYMENT_FAILED_QUEUE)
    public void handlePaymentFailed(PaymentFailedEvent event) {
        System.out.println(">>> [Orchestrator] PaymentFailedEvent received for OrderID: " + event.getOrderId());

        // La saga falla -> Cancelar la orden
        updateOrderStatus(event.getOrderId(), "CANCELLED");
        System.out.println(">>> [Orchestrator] Saga ended in CANCELLED due to Payment fail for OrderID: " + event.getOrderId());
    }

    @RabbitListener(queues = RabbitMQConfig.ORCH_INVENTORY_RESERVED_QUEUE)
    public void handleInventoryReserved(InventoryReservedEvent event) {
        System.out.println(">>> [Orchestrator] InventoryReservedEvent received for OrderID: " + event.getOrderId());

        orderItemsCache.remove(event.getOrderId());

        // Saga completada con éxito -> Order "COMPLETED"
        updateOrderStatus(event.getOrderId(), "COMPLETED");
        System.out.println(">>> [Orchestrator] Saga COMPLETED for OrderID: " + event.getOrderId());
    }

    // Escuchar "InventoryReservationFailedEvent"
    @RabbitListener(queues = RabbitMQConfig.ORCH_INVENTORY_RESERVE_FAILED_QUEUE)
    public void handleInventoryReservationFailed(InventoryReservationFailedEvent event) {
        System.out.println(">>> [Orchestrator] InventoryReservationFailedEvent for OrderID: " + event.getOrderId());

        updateOrderStatus(event.getOrderId(), "CANCELLED");
        System.out.println(">>> [Orchestrator] Saga CANCELLED for OrderID: " + event.getOrderId());
    }

    // Helper para actualizar el estado de la orden en Order MS
    private void updateOrderStatus(Integer orderId, String newStatus) {
        OrderStatusUpdateEvent updateEvent = new OrderStatusUpdateEvent(orderId, newStatus);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_STATUS_UPDATE_RK,
                updateEvent
        );
    }
}

