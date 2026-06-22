package com.example.Auth_Service.exception;

public class BadCredentialsException extends  RuntimeException{
    public BadCredentialsException(String message) {
        super(message);
    }
}
