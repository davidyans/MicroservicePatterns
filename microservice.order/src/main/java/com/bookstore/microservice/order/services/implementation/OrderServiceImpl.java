package com.bookstore.microservice.order.services.implementation;

import com.bookstore.microservice.order.clients.BookCatalogClient;
import com.bookstore.microservice.order.config.RabbitMQConfig;
import com.bookstore.microservice.order.domain.Order;
import com.bookstore.microservice.order.dto.BookDTO;
import com.bookstore.microservice.order.dto.OrderDTO;
import com.bookstore.microservice.order.dto.OrderDetailDTO;
import com.bookstore.microservice.order.events.OrderCreatedEvent;
import com.bookstore.microservice.order.exceptions.ResourceNotFoundException;
import com.bookstore.microservice.order.mappers.OrderMapper;
import com.bookstore.microservice.order.repository.OrderRepository;
import com.bookstore.microservice.order.services.OrderService;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final String BOOKCATALOG_CB = "bookcatalogClient";

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private BookCatalogClient bookCatalogClient;

    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::toOrderDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        return OrderMapper.toOrderDTO(order);
    }

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setStatus("CREATED");

        validateBooksExist(orderDTO.getOrderDetails());

        for (OrderDetailDTO detailDTO : orderDTO.getOrderDetails()) {
            order.addItem(detailDTO.getBookId(), detailDTO.getQuantity(), detailDTO.getUnitPrice());
        }

        order = orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                order.getOrderId(),
                order.getTotal(),
                orderDTO.getOrderDetails()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_CREATED_RK,
                event
        );

        return OrderMapper.toOrderDTO(order);
    }

    @Override
    public OrderDTO updateOrder(Integer orderId, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!existingOrder.getStatus().equals(orderDTO.getStatus())) {
            existingOrder.setStatus(orderDTO.getStatus());
            existingOrder.markAsUpdated();
        }

        validateBooksExist(orderDTO.getOrderDetails());

        existingOrder.clearItems();
        for (OrderDetailDTO detailDTO : orderDTO.getOrderDetails()) {
            existingOrder.addItem(detailDTO.getBookId(), detailDTO.getQuantity(), detailDTO.getUnitPrice());
        }

        return OrderMapper.toOrderDTO(orderRepository.save(existingOrder));
    }

    @Override
    public List<OrderDetailDTO> getOrderDetails(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        return order.getOrderDetails().stream()
                .map(OrderMapper::toOrderDetailDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteOrder(Integer orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order not found with ID: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    @CircuitBreaker(name = BOOKCATALOG_CB, fallbackMethod = "bookFallbackList")
    @Retry(name = BOOKCATALOG_CB)
    private void validateBooksExist(List<OrderDetailDTO> orderDetails) {
        List<Integer> bookIds = orderDetails.stream()
                .map(OrderDetailDTO::getBookId)
                .collect(Collectors.toList());

        for (Integer bookId : bookIds) {
            validateBookExists(bookId);
        }
    }

    @CircuitBreaker(name = BOOKCATALOG_CB, fallbackMethod = "bookFallbackSingle")
    @Retry(name = BOOKCATALOG_CB)
    private void validateBookExists(Integer bookId) {
        try {
            BookDTO book = bookCatalogClient.getBookById(bookId);
            if (book == null) {
                throw new ResourceNotFoundException("Book not found with ID: " + bookId);
            }
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Book not found with ID: " + bookId);
        } catch (FeignException e) {
            throw new RuntimeException("Error al comunicarse con Bookcatalog Service: " + e.getMessage());
        }
    }

    private void bookFallbackList(List<OrderDetailDTO> orderDetails, Throwable throwable) {
        throw new RuntimeException("Bookcatalog Service está inactivo. Por favor, intenta más tarde.");
    }

    private void bookFallbackSingle(Integer bookId, Throwable throwable) {
        throw new RuntimeException("Bookcatalog Service está inactivo. Por favor, intenta más tarde.");
    }
}
