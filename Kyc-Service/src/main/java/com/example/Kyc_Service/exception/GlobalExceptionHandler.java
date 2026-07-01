package com.example.Kyc_Service.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(KycException.class)
    public ResponseEntity<Map<String,Object>> handleKycException(
            KycException ex) {
        return ResponseEntity.status(400)
                .body(Map.of(
                        "timestamp", java.time.LocalDateTime.now(),
                        "status", 400,
                        "error", "kyc Exception",
                        "message", ex.getMessage()
                ));
    }




}