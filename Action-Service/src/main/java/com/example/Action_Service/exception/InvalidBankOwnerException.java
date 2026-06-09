package com.example.Action_Service.exception;

public class InvalidBankOwnerException extends  RuntimeException{
    public InvalidBankOwnerException(String message){
        super(message);
    }
}
