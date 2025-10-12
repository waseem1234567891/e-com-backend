package com.chak.E_Commerce_Back_End.dto.order;

import com.chak.E_Commerce_Back_End.model.OrderItem;

public class OrderItemDTO {
    private Long productId;
    private Integer quantity;
    private String productName;
    private String imageUrl;
    private Double productPrice;

    public OrderItemDTO()
    {

    }
    public OrderItemDTO(OrderItem orderItem)
    {
        this.productId=orderItem.getProduct().getId();
        this.quantity=orderItem.getQuantity();
        this.productName=orderItem.getProduct().getName();
        this.imageUrl=orderItem.getProduct().getImagePath();
        this.productPrice=orderItem.getProduct().getPrice();
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }
}
