package com.chak.E_Commerce_Back_End.exception;

public class OrderAlreadyCancelled extends RuntimeException{
    public OrderAlreadyCancelled(String message)
    {
        super(message);
    }
}
