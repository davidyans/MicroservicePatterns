package com.bookstore.microservice.order.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class OrderDetailId implements Serializable {
    private Integer orderId;
    private Integer bookId;
}
