package com.chak.E_Commerce_Back_End.dto.order;

import com.chak.E_Commerce_Back_End.model.Order;
import com.chak.E_Commerce_Back_End.model.enums.OrderStatus;

import java.util.List;
import java.util.stream.Collectors;

public class OrderDTO {
    private String guestName;
    private String guestEmail;
    private String shippingAddress; // combined string
    private String paymentMethod;
    private List<OrderItemDTO> items;
    private Double totalAmount;// optional if calculated in backend
    private OrderStatus orderStatus;

    public OrderDTO()
    {}
    public OrderDTO(Order order)
    {
        if(order.getUser()!=null)
        {
            this.shippingAddress=order.getShippingAddress();
            this.paymentMethod=order.getPaymentMethod();
            this.totalAmount=order.getTotalAmount();
            this.items=order.getItems().stream().map(OrderItemDTO::new).collect(Collectors.toList());
            this.orderStatus=order.getStatus();

        }
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

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

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
