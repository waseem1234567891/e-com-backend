package com.chak.E_Commerce_Back_End.model.enums;

public enum OrderStatus {
    PENDING,        // Order placed by user (initial status)
    CONFIRMED,      // Accepted by admin/seller
    PROCESSING,     // Being prepared/packed
    SHIPPED,        // Dispatched from seller
    DELIVERED,      // Successfully delivered
    CANCELLED,      // Cancelled before shipping
    RETURNED        // Returned after delivery
}
