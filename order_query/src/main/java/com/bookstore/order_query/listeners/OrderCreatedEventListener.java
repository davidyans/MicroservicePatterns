package com.bookstore.order_query.listeners;

import com.bookstore.order_query.config.RabbitMQConfigQuery;
import com.bookstore.order_query.domain.OrderHistory;
import com.bookstore.order_query.domain.OrderHistoryDetail;
import com.bookstore.order_query.dto.OrderDetailDTO;
import com.bookstore.order_query.events.OrderCreatedEvent;
import com.bookstore.order_query.repository.OrderHistoryRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class OrderCreatedEventListener {

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @RabbitListener(queues = RabbitMQConfigQuery.ORDER_CREATED_QUEUE_QUERY)
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Evitar duplicados si ya existe
        var existingOpt = orderHistoryRepository.findByOrderId(event.getOrderId());
        if (existingOpt.isPresent()) {
            System.out.println("[order-query] La orden " + event.getOrderId() + " ya existe. Ignorar.");
            return;
        }

        // Crear OrderHistory
        OrderHistory oh = new OrderHistory();
        oh.setOrderId(event.getOrderId());
        oh.setUserId(event.getUserId());
        oh.setOrderDate(LocalDateTime.now());
        // Podrías usar la fecha que venga en el evento si existe
        oh.setTotal(event.getTotal() != null ? event.getTotal() : BigDecimal.ZERO);
        oh.setStatus("CREATED");
        oh.setUpdatedDate(LocalDateTime.now());

        // Construir detalles
        var details = new ArrayList<OrderHistoryDetail>();
        if (event.getOrderDetails() != null) {
            for (OrderDetailDTO detailDTO : event.getOrderDetails()) {
                OrderHistoryDetail detail = new OrderHistoryDetail();
                detail.setOrderHistory(oh);
                detail.setBookId(detailDTO.getBookId());
                detail.setQuantity(detailDTO.getQuantity());
                detail.setUnitPrice(detailDTO.getUnitPrice());
                details.add(detail);
            }
        }
        oh.setOrderDetails(details);

        // Guardar
        orderHistoryRepository.save(oh);
        System.out.println("[order-query] Procesado OrderCreatedEvent. Orden "
                + event.getOrderId() + " guardada en order_history.");
    }
}
