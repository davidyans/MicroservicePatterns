package com.bookstore.order_command.services.implementation;

import com.bookstore.order_command.config.RabbitMQConfigCommand;
import com.bookstore.order_command.domain.Order;
import com.bookstore.order_command.dto.OrderDTO;
import com.bookstore.order_command.events.OrderCreatedEvent;
import com.bookstore.order_command.mappers.OrderMapper;
import com.bookstore.order_command.repository.OrderRepository;
import com.bookstore.order_command.services.OrderCommandService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderCommandServiceImpl implements OrderCommandService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        // 1. Guardar la orden en la BD local
        Order order = new Order();
        order.setStatus("CREATED");

        Order finalOrder = order;
        orderDTO.getOrderDetails().forEach(detailDTO -> {
            finalOrder.addItem(
                    detailDTO.getBookId(),
                    detailDTO.getQuantity(),
                    detailDTO.getUnitPrice()
            );
        });

        order = orderRepository.save(order);

        // 2. Publicar evento
        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getOrderId(),
                order.getTotal(),
                orderDTO.getOrderDetails() // lista de OrderDetailDTO
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfigCommand.ORDER_EXCHANGE,
                RabbitMQConfigCommand.ORDER_CREATED_RK,
                event
        );

        System.out.println("[order-command] Publicado OrderCreatedEvent para orderId=" + order.getOrderId());

        return OrderMapper.toOrderDTO(order);
    }
}

