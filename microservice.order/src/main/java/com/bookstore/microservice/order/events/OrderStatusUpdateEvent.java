package com.bookstore.microservice.order.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusUpdateEvent implements Serializable {
    private Integer orderId;
    private String newStatus; // COMPLETED, CANCELLED
}
