package com.bookstore.microservice.payment.services;

import com.bookstore.microservice.payment.domain.Payment;
import com.bookstore.microservice.payment.dto.PaymentDTO;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {
    List<PaymentDTO> getAllPayments();
    PaymentDTO getPaymentById(Integer paymentId);
    PaymentDTO createPayment(PaymentDTO paymentDTO);
    PaymentDTO updatePayment(Integer paymentId, PaymentDTO paymentDTO);
    void deletePayment(Integer paymentId);
}

