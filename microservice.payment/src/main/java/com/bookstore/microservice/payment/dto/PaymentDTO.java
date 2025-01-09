package com.bookstore.microservice.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Integer paymentId;
    private Integer orderId;
    private LocalDateTime paymentDate;
    private String payMethod;
    private String status;
    private BigDecimal amount;
    private String paymentDetails;
}
