package com.chak.E_Commerce_Back_End.dto.cart;

public class UpdateQuantityRequest {
    private int quantity;

    public UpdateQuantityRequest() {}  // Required for Spring deserialization

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
