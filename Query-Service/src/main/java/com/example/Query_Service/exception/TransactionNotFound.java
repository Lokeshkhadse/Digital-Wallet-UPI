package com.example.Query_Service.exception;

public class TransactionNotFound extends RuntimeException{
    public TransactionNotFound(String message) {
        super(message);
    }
}
