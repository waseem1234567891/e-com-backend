package com.chak.E_Commerce_Back_End.exception;

public class PasswordInCorrectException extends RuntimeException{
    public PasswordInCorrectException(String message)
    {
        super(message);
    }
}
