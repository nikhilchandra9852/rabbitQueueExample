package com.messagiing.producerservice.dto;

import java.time.LocalDate;
import java.util.Date;

public class OrderCreateDto {

    String orderId;
    String customerId;
    LocalDate orderDate;
    Double orderAmount;

    public OrderCreateDto() {
    }

    public OrderCreateDto(String orderId, String customerId, Double orderAmount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = LocalDate.now();
        this.orderAmount = orderAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    @Override
    public String toString() {
        return "OrderCreateDto{" +
                "orderId='" + orderId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", orderDate=" + orderDate +
                ", orderAmount=" + orderAmount +
                '}';
    }
}
