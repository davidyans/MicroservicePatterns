package com.bookstore.microservice.payment.services.implementation;

import com.bookstore.microservice.payment.config.OrderClient;
import com.bookstore.microservice.payment.domain.Payment;
import com.bookstore.microservice.payment.dto.OrderDTO;
import com.bookstore.microservice.payment.dto.PaymentDTO;
import com.bookstore.microservice.payment.exceptions.ResourceNotFoundException;
import com.bookstore.microservice.payment.repository.PaymentRepository;
import com.bookstore.microservice.payment.services.PaymentService;
import com.bookstore.microservice.payment.mappers.PaymentMapper;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final String ORDER_CB = "orderClient";

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderClient orderClient;

    @Override
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(PaymentMapper::toPaymentDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDTO getPaymentById(Integer paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
        return PaymentMapper.toPaymentDTO(payment);
    }

    @Override
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        validateOrderExists(paymentDTO.getOrderId());

        Payment payment = PaymentMapper.toPaymentEntity(paymentDTO);
        payment.setPaymentDate(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);
        return PaymentMapper.toPaymentDTO(savedPayment);
    }

    @Override
    public PaymentDTO updatePayment(Integer paymentId, PaymentDTO paymentDTO) {
        Payment existingPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        if (!existingPayment.getOrderId().equals(paymentDTO.getOrderId())) {
            validateOrderExists(paymentDTO.getOrderId());
            existingPayment.setOrderId(paymentDTO.getOrderId());
        }

        existingPayment.setPayMethod(paymentDTO.getPayMethod());
        existingPayment.setStatus(paymentDTO.getStatus());
        existingPayment.setAmount(paymentDTO.getAmount());
        existingPayment.setPaymentDetails(paymentDTO.getPaymentDetails());
        existingPayment.setPaymentDate(paymentDTO.getPaymentDate());

        Payment updatedPayment = paymentRepository.save(existingPayment);
        return PaymentMapper.toPaymentDTO(updatedPayment);
    }

    @Override
    public void deletePayment(Integer paymentId) {
        if (!paymentRepository.existsById(paymentId)) {
            throw new ResourceNotFoundException("Payment not found with ID: " + paymentId);
        }
        paymentRepository.deleteById(paymentId);
    }

    @CircuitBreaker(name = ORDER_CB, fallbackMethod = "orderFallbackSingle")
    @Retry(name = ORDER_CB)
    private void validateOrderExists(Integer orderId) {
        try {
            OrderDTO order = orderClient.getOrderById(orderId);
            if (order == null) {
                throw new ResourceNotFoundException("Order not found with ID: " + orderId);
            }
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Order not found with ID: " + orderId);
        } catch (FeignException e) {
            throw new RuntimeException("Error al comunicarse con Order Service: " + e.getMessage());
        }
    }

    private void orderFallbackSingle(Integer orderId, Throwable throwable) {
        throw new RuntimeException("Order Service está inactivo. Por favor, intenta más tarde.");
    }
}
