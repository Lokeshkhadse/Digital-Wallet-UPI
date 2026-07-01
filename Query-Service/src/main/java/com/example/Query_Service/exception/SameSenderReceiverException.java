package com.example.Query_Service.exception;

public class SameSenderReceiverException extends RuntimeException
{
    public SameSenderReceiverException(String message) {
        super(message);
    }
}
