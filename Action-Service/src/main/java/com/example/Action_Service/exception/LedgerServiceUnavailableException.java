package com.example.Action_Service.exception;

public class LedgerServiceUnavailableException extends RuntimeException {

    public LedgerServiceUnavailableException(String message) {
        super(message);
    }
}