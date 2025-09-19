package com.chak.E_Commerce_Back_End.exception;

// Thrown when user tries to register with an existing email
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
