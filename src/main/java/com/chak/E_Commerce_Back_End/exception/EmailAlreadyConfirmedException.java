package com.chak.E_Commerce_Back_End.exception;

public class EmailAlreadyConfirmedException extends RuntimeException{
    public  EmailAlreadyConfirmedException(String message)
    {
        super(message);
    }
}
