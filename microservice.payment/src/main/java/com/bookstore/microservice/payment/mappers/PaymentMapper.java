package com.bookstore.microservice.payment.mappers;

import com.bookstore.microservice.payment.domain.Payment;
import com.bookstore.microservice.payment.dto.PaymentDTO;

public class PaymentMapper {

    public static PaymentDTO toPaymentDTO(Payment payment) {
        if (payment == null) {
            return null;
        }
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setOrderId(payment.getOrderId());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setPayMethod(payment.getPayMethod());
        dto.setStatus(payment.getStatus());
        dto.setAmount(payment.getAmount());
        dto.setExternalTransactionId(payment.getExternalTransactionId());
        dto.setPaymentDetails(payment.getPaymentDetails());
        return dto;
    }

    public static Payment toPaymentEntity(PaymentDTO dto) {
        if (dto == null) {
            return null;
        }
        Payment payment = new Payment();
        payment.setPaymentId(dto.getPaymentId());
        payment.setOrderId(dto.getOrderId());
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setPayMethod(dto.getPayMethod());
        payment.setStatus(dto.getStatus());
        payment.setAmount(dto.getAmount());
        payment.setExternalTransactionId(dto.getExternalTransactionId());
        payment.setPaymentDetails(dto.getPaymentDetails());
        return payment;
    }
}
