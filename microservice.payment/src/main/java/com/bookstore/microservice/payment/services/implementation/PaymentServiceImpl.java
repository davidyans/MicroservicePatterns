package com.bookstore.microservice.payment.services.implementation;

import com.bookstore.microservice.payment.domain.Payment;
import com.bookstore.microservice.payment.dto.PaymentDTO;
import com.bookstore.microservice.payment.exceptions.ResourceNotFoundException;
import com.bookstore.microservice.payment.repository.PaymentRepository;
import com.bookstore.microservice.payment.services.PaymentService;
import com.bookstore.microservice.payment.mappers.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

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
        Payment payment = PaymentMapper.toPaymentEntity(paymentDTO);
        payment.setPaymentDate(LocalDateTime.now());
        Payment savedPayment = paymentRepository.save(payment);
        return PaymentMapper.toPaymentDTO(savedPayment);
    }

    @Override
    public PaymentDTO updatePayment(Integer paymentId, PaymentDTO paymentDTO) {
        Payment existingPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

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
}
