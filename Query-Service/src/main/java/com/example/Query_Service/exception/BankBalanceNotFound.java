package com.example.Query_Service.exception;

public class BankBalanceNotFound extends RuntimeException{

    public BankBalanceNotFound(String message) {
        super(message);
    }
}
