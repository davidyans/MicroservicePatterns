package com.bookstore.microservice.payment.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent implements Serializable {
    private Integer orderId;
    private Integer paymentId;
    private String status; // "COMPLETED"
}
