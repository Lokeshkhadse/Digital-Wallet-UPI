package com.example.Action_Service.exception;

public class RabbitMQPublishException
        extends RuntimeException {

    public RabbitMQPublishException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}