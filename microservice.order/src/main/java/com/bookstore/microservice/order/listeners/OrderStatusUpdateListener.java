package com.bookstore.microservice.order.listeners;

import com.bookstore.microservice.order.config.RabbitMQConfig;
import com.bookstore.microservice.order.domain.Order;
import com.bookstore.microservice.order.events.OrderStatusUpdateEvent;
import com.bookstore.microservice.order.repository.OrderRepository;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderStatusUpdateListener {

    @Autowired
    private OrderRepository orderRepository;

    @RabbitListener(queues = RabbitMQConfig.ORDER_STATUS_UPDATE_QUEUE)
    public void handleOrderStatusUpdate(OrderStatusUpdateEvent event) {
        Optional<Order> optionalOrder = orderRepository.findById(event.getOrderId());
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(event.getNewStatus());
            order.markAsUpdated();
            orderRepository.save(order);
            System.out.println("Order " + order.getOrderId() + " updated to status: " + event.getNewStatus());
        } else {
            System.out.println("Order not found: " + event.getOrderId());
        }
    }
}
