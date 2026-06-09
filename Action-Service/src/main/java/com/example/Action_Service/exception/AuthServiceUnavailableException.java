package com.example.Action_Service.exception;

public class AuthServiceUnavailableException extends  RuntimeException{
    public AuthServiceUnavailableException(String message){
        super(message);
    }
}
