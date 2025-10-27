package com.chak.E_Commerce_Back_End.exception;

public class ProductCategoryNotFound  extends RuntimeException{
    public ProductCategoryNotFound(String message)
    {
        super(message);
    }
}
