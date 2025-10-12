package com.chak.E_Commerce_Back_End.dto.order;

import com.chak.E_Commerce_Back_End.model.Order;
import com.chak.E_Commerce_Back_End.model.enums.OrderStatus;

import java.time.LocalDateTime;

public class CustomOrderDto {
    private Long id;
    private String userName; // 👈 only this, not full User
    private Double totalAmount;
    private OrderStatus status;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime orderDate;

    public CustomOrderDto(Order order)
    {
        this.id=order.getId();
        this.userName=order.getUser().getFirstName()+" "+order.getUser().getLastName();
        this.totalAmount=order.getTotalAmount();
        this.status=order.getStatus();
        this.paymentStatus=order.getPaymentStatus();
        this.paymentMethod=order.getPaymentMethod();
        this.orderDate=order.getOrderDate();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
}
