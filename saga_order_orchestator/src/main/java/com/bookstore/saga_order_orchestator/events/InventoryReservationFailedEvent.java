package com.bookstore.saga_order_orchestator.events;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservationFailedEvent implements Serializable {
    private Integer orderId;
    private String reason;
    private List<ReserveInventoryItem> failedItems;

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<ReserveInventoryItem> getFailedItems() {
        return failedItems;
    }

    public void setFailedItems(List<ReserveInventoryItem> failedItems) {
        this.failedItems = failedItems;
    }
}
