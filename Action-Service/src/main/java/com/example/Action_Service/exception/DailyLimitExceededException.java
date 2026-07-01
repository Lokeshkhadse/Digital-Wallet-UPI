package com.example.Action_Service.exception;

public class DailyLimitExceededException extends RuntimeException{
    public DailyLimitExceededException(String message){
        super(message);
    }
}
