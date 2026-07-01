package com.example.Action_Service.exception;

public class PinLockedException extends RuntimeException {

    public PinLockedException(String message) {
        super(message);
    }
}