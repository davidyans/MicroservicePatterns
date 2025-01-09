package com.bookstore.microservice.order.events;

import com.bookstore.microservice.order.dto.OrderDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent implements Serializable {
    private Integer orderId;
    private BigDecimal total;
    private List<OrderDetailDTO> orderDetails;
}
