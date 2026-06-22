package com.example.Action_Service.exception;

public class BeneficiaryAlreadyExistsException
        extends RuntimeException {

    public BeneficiaryAlreadyExistsException(
            String message) {

        super(message);
    }
}
