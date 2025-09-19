package com.chak.E_Commerce_Back_End.exception;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(String message)
    {
        super(message);
    }
}
