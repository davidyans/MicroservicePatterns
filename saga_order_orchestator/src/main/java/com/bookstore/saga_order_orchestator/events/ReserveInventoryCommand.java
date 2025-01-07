package com.bookstore.saga_order_orchestator.events;

import java.io.Serializable;
import java.util.List;

public class ReserveInventoryCommand implements Serializable {
    private Integer orderId;
    private List<ReserveInventoryItem> items;

    public ReserveInventoryCommand() {
    }

    public ReserveInventoryCommand(Integer orderId, List<ReserveInventoryItem> items) {
        this.orderId = orderId;
        this.items = items;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public List<ReserveInventoryItem> getItems() {
        return items;
    }

    public void setItems(List<ReserveInventoryItem> items) {
        this.items = items;
    }
}
