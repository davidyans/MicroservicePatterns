package com.bookstore.order_query.listeners;

import com.bookstore.order_query.config.RabbitMQConfigQuery;
import com.bookstore.order_query.domain.OrderHistory;
import com.bookstore.order_query.events.OrderStatusUpdateEvent;
import com.bookstore.order_query.repository.OrderHistoryRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderStatusUpdateEventListener {

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @RabbitListener(queues = RabbitMQConfigQuery.ORDER_STATUS_UPDATE_QUEUE_QUERY)
    public void handleOrderStatusUpdate(OrderStatusUpdateEvent event) {
        var optional = orderHistoryRepository.findByOrderId(event.getOrderId());
        if (optional.isPresent()) {
            OrderHistory oh = optional.get();
            oh.setStatus(event.getNewStatus());
            oh.setUpdatedDate(LocalDateTime.now());
            orderHistoryRepository.save(oh);

            System.out.println("[order-query] Procesado OrderStatusUpdateEvent. " +
                    "Orden " + event.getOrderId() + " => status: " + event.getNewStatus());
        } else {
            System.out.println("[order-query] No existe order_history para orderId="
                    + event.getOrderId() + ". Ignorando evento.");
        }
    }
}
