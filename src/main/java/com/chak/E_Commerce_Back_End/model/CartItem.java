package com.chak.E_Commerce_Back_End.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CartItem {
    @Id
    private Long productId;
    private String productName;
    private String productImage;
    private Double productPrice;
    private int quantity;

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
