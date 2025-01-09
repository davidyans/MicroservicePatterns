package com.bookstore.order_command.events;

import com.bookstore.order_command.dto.OrderDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class OrderCreatedEvent implements Serializable {
    private Integer orderId;
    private BigDecimal total;
    private List<OrderDetailDTO> orderDetails;

    public OrderCreatedEvent() {
    }

    public OrderCreatedEvent(Integer orderId, BigDecimal total, List<OrderDetailDTO> orderDetails) {
        this.orderId = orderId;
        this.total = total;
        this.orderDetails = orderDetails;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<OrderDetailDTO> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailDTO> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
