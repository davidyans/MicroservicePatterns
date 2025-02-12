package com.bookstore.order_command.listeners;

import com.bookstore.order_command.config.RabbitMQConfigCommand;
import com.bookstore.order_command.domain.Order;
import com.bookstore.order_command.events.OrderCreatedEvent;
import com.bookstore.order_command.repository.OrderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderCreatedListenerFromOriginal {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    @RabbitListener(queues = RabbitMQConfigCommand.ORDER_CREATED_QUEUE_COMMAND, containerFactory = "rabbitListenerContainerFactory")
    public void handleOrderCreated(OrderCreatedEvent event) {
        boolean exists = orderRepository.existsById(event.getOrderId());
        if (!exists) {
            Order newOrder = new Order();
            newOrder.setOrderId(event.getOrderId());
            newOrder.setStatus("CREATED");
            newOrder.setOrderDate(java.time.LocalDateTime.now());

            // Llenar detalles
            if (event.getOrderDetails() != null) {
                event.getOrderDetails().forEach(detail -> {
                    newOrder.addItem(
                            detail.getBookId(),
                            detail.getQuantity(),
                            detail.getUnitPrice()
                    );
                });
            }

            orderRepository.save(newOrder);
            System.out.println("[order-command] Recibido OrderCreatedEvent desde MS original. Guardada orden "
                    + event.getOrderId());
        } else {
            System.out.println("[order-command] Orden " + event.getOrderId()
                    + " ya existe. Ignorando el evento.");
        }
    }
}

