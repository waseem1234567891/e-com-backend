package com.chak.E_Commerce_Back_End.dto;

import com.chak.E_Commerce_Back_End.model.OrderItem;

import java.util.List;

public class OrderDTO {

    private String shippingAddress; // combined string
    private String paymentMethod;
    private List<OrderItemDTO> items;
    private Double totalAmount; // optional if calculated in backend

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
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



    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
