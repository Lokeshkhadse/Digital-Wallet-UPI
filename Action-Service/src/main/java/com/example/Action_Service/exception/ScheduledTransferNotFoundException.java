package com.example.Action_Service.exception;

public class ScheduledTransferNotFoundException
        extends RuntimeException {

    public ScheduledTransferNotFoundException(
            String message) {
        super(message);
    }
}