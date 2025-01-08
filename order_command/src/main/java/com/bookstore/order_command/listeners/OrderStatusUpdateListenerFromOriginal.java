package com.bookstore.order_command.listeners;

import com.bookstore.order_command.config.RabbitMQConfigCommand;
import com.bookstore.order_command.domain.Order;
import com.bookstore.order_command.events.OrderStatusUpdateEvent;
import com.bookstore.order_command.repository.OrderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusUpdateListenerFromOriginal {

    @Autowired
    private OrderRepository orderRepository;

    @RabbitListener(queues = RabbitMQConfigCommand.ORDER_STATUS_UPDATE_QUEUE_COMMAND)
    public void handleOrderStatusUpdate(OrderStatusUpdateEvent event) {
        // Buscar la orden en la BD local de order-command
        var optional = orderRepository.findById(event.getOrderId());
        if (optional.isPresent()) {
            Order existingOrder = optional.get();
            existingOrder.setStatus(event.getNewStatus());
            existingOrder.markAsUpdated();
            orderRepository.save(existingOrder);

            System.out.println("[order-command] Recibido OrderStatusUpdateEvent desde MS original. " +
                    "Orden " + event.getOrderId() + " actualizada a " + event.getNewStatus());
        } else {
            System.out.println("[order-command] No existe orden " + event.getOrderId()
                    + " en la BD local. Ignorando.");
        }
    }
}

