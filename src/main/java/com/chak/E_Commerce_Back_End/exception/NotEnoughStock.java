package com.chak.E_Commerce_Back_End.exception;

public class NotEnoughStock extends RuntimeException{
    public NotEnoughStock(String message)
    {
        super(message);
    }
}
