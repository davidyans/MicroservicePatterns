package com.bookstore.order_command.listeners;

import com.bookstore.order_command.config.RabbitMQConfigCommand;
import com.bookstore.order_command.domain.Order;
import com.bookstore.order_command.events.OrderCreatedEvent;
import com.bookstore.order_command.repository.OrderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderCreatedListenerFromOriginal {

    @Autowired
    private OrderRepository orderRepository;  // Repositorio JPA de este nuevo microservicio
    // O el servicio que maneje la lógica

    @RabbitListener(queues = RabbitMQConfigCommand.ORDER_CREATED_QUEUE_COMMAND)
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Verificamos si la orden ya existe aquí, para evitar duplicados
        boolean exists = orderRepository.existsById(event.getOrderId());
        if (!exists) {
            // Crear y guardar la orden en la BD local de order-command
            Order newOrder = new Order();
            newOrder.setOrderId(event.getOrderId()); // asumiendo que en la entidad uses manual set
            newOrder.setUserId(event.getUserId());
            newOrder.setStatus("CREATED");
            newOrder.setOrderDate(java.time.LocalDateTime.now());
            // O si quieres usar un campo event.getOrderDate() si lo tuvieras

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

