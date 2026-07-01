package com.example.Action_Service.exception;

public class BankNotFoundException extends RuntimeException{
    public BankNotFoundException(String message) {
        super(message);
    }
}
