package com.example.Query_Service.exception;

public class QrNotFoundException
        extends RuntimeException {

    public QrNotFoundException(
            String message) {
        super(message);
    }
}