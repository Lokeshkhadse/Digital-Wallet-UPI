package com.example.Notification_Service.exception;

public class FailedToSendEmail extends RuntimeException{
    public FailedToSendEmail(String message){
        super(message);
    }
}
