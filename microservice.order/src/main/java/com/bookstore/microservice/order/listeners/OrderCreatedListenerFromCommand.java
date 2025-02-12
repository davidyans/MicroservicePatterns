package com.bookstore.microservice.order.listeners;

import com.bookstore.microservice.order.config.RabbitMQConfig;
import com.bookstore.microservice.order.domain.Order;
import com.bookstore.microservice.order.events.OrderCreatedEvent;
import com.bookstore.microservice.order.repository.OrderRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderCreatedListenerFromCommand {

    @Autowired
    private OrderRepository orderRepository;

    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE_FROM_COMMAND)
    public void handleOrderCreatedFromCommand(OrderCreatedEvent event) {
        boolean exists = orderRepository.existsById(event.getOrderId());
        if (!exists) {
            Order newOrder = new Order();
            newOrder.setOrderId(event.getOrderId());
            newOrder.setStatus("CREATED");
            newOrder.setOrderDate(java.time.LocalDateTime.now());

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
            System.out.println("[order-ms] Recibido OrderCreatedEvent desde order-command. Orden guardada: "
                    + event.getOrderId());
        } else {
            System.out.println("[order-ms] La orden " + event.getOrderId()
                    + " ya existe en BD. Ignorando evento.");
        }
    }
}

