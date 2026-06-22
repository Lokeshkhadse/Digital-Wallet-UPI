package com.example.Action_Service.exception;

public class NotificationProcessingException
        extends RuntimeException {

    public NotificationProcessingException(
            String message) {

        super(message);
    }
}
