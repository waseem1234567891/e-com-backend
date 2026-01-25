package com.chak.E_Commerce_Back_End.model.enums;

public enum UserStatus {
    PENDING,    // Registered but email not confirmed
    ACTIVE,     // Email confirmed, can log in
    DISABLED,   // Admin blocked
    DELETED     // Soft delete (optional)
}
