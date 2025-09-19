package com.chak.E_Commerce_Back_End.dto.order;

import com.chak.E_Commerce_Back_End.model.OrderItem;
import com.chak.E_Commerce_Back_End.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDTO {
    private Long id;
    private String userName; // 👈 only this, not full User
    private Double totalAmount;
    private OrderStatus status;
    private String paymentStatus;
    private String shippingAddress;
    private String paymentMethod;
    private LocalDateTime orderDate;
    private List<OrderItem> items; // optional if you want item info

    // getters and setters


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
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

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
