package com.bookstore.saga_order_orchestator.events;

import java.io.Serializable;

public class OrderStatusUpdateEvent implements Serializable {
    private Integer orderId;
    private String newStatus; // COMPLETED, CANCELLED

    public OrderStatusUpdateEvent() {
    }

    public OrderStatusUpdateEvent(Integer orderId, String newStatus) {
        this.orderId = orderId;
        this.newStatus = newStatus;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
}
