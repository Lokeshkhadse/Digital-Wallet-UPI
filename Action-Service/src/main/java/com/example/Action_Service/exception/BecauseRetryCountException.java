package com.example.Action_Service.exception;

public class BecauseRetryCountException extends RuntimeException{
    public BecauseRetryCountException(String message){
        super(message);
    }
}
