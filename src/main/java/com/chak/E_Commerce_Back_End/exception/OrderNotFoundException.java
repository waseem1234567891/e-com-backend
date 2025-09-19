package com.chak.E_Commerce_Back_End.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message)
    {
        super(message);
    }
}
