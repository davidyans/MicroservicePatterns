package com.bookstore.order_command.listeners;

import com.bookstore.order_command.config.RabbitMQConfigCommand;
import com.bookstore.order_command.domain.Order;
import com.bookstore.order_command.events.OrderStatusUpdateEvent;
import com.bookstore.order_command.repository.OrderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderStatusUpdateListenerFromOriginal {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    @RabbitListener(queues = RabbitMQConfigCommand.ORDER_STATUS_UPDATE_QUEUE_COMMAND)
    public void handleOrderStatusUpdate(OrderStatusUpdateEvent event) {
        try {
            orderRepository.findById(event.getOrderId()).ifPresentOrElse(existingOrder -> {
                existingOrder.setStatus(event.getNewStatus());
                existingOrder.markAsUpdated();
                orderRepository.save(existingOrder);

                System.out.println("[order-command] Recibido OrderStatusUpdateEvent desde MS original. Orden " +
                        event.getOrderId() + " actualizada a " + event.getNewStatus());
            }, () -> {
                System.out.println("[order-command] No existe orden " + event.getOrderId() + " en la BD local. Ignorando.");
            });
        } catch (ObjectOptimisticLockingFailureException e) {
            System.out.println("[order-command] Conflicto de concurrencia al actualizar la orden " + event.getOrderId() +
                    ". Reintentando...");
            handleOrderStatusUpdate(event); // Reintento manual
        }
    }
}
