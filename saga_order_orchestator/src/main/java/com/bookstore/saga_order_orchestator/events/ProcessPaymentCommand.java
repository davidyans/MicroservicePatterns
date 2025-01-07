package com.bookstore.saga_order_orchestator.events;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProcessPaymentCommand implements Serializable {
    private Integer orderId;
    private BigDecimal amount;
    private String payMethod;

    public ProcessPaymentCommand() {
    }

    public ProcessPaymentCommand(Integer orderId, BigDecimal amount, String payMethod) {
        this.orderId = orderId;
        this.amount = amount;
        this.payMethod = payMethod;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }
}
