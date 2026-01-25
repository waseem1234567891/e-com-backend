package com.chak.E_Commerce_Back_End.exception;

public class EmailNotVerifiedException extends RuntimeException{
    public EmailNotVerifiedException(String message)
    {
        super(message);
    }
}
