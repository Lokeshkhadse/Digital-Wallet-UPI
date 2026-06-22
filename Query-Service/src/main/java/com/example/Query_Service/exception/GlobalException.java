package com.example.Query_Service.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> userNotFoundExceptionHandler(
            UserNotFoundException ex) {

        return ResponseEntity.status(404)
                .body(Map.of(
                        "timestamp", java.time.LocalDateTime.now(),
                        "status", 404,
                        "error", "User Not Found",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(BankNotFoundException.class)
    public ResponseEntity<Map<String, Object>> bankNotFoundExceptionHandler(
            BankNotFoundException ex) {
        return ResponseEntity.status(404)
                .body(Map.of(
                        "timestamp", java.time.LocalDateTime.now(),
                        "status", 404,
                        "error", "Bank Not Found",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> resourceNotFoundExceptionHandler(
            ResourceNotFoundException ex) {
        return ResponseEntity.status(404)
                .body(Map.of(
                        "timestamp", java.time.LocalDateTime.now(),
                        "status", 404,
                        "error", "Resource Not Found",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String,Object>> duplicateResourceExceptionHandler(
            DuplicateResourceException ex) {
        return ResponseEntity.status(409)
                .body(Map.of(
                        "timestamp", java.time.LocalDateTime.now(),
                        "status", 409,
                        "error", "Duplicate Resource",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(SameSenderReceiverException.class)
    public ResponseEntity<Map<String,Object>> sameSenderReceiverExceptionHandler(
            SameSenderReceiverException ex) {
        return ResponseEntity.status(400)
                .body(Map.of(
                        "timestamp", java.time.LocalDateTime.now(),
                        "status", 400,
                        "error", "Invalid Transaction",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String,Object>> insufficientBalanceExceptionHandler(
            InsufficientBalanceException ex) {
        return ResponseEntity.status(400)
                .body(Map.of(
                        "timestamp", java.time.LocalDateTime.now(),
                        "status", 400,
                        "error", "Insufficient Balance",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String,Object>> validationExceptionHandler(
            ValidationException ex) {
        return ResponseEntity.status(400)
                .body(Map.of(
                        "timestamp", java.time.LocalDateTime.now(),
                        "status", 400,
                        "error", "Validation Error",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(BankBalanceNotFound.class)
    public ResponseEntity<Map<String,Object>> bankBalanceNotFoundExceptionHandler(
            BankBalanceNotFound ex) {
        return ResponseEntity.status(404)
                .body(Map.of(
                        "timestamp", java.time.LocalDateTime.now(),
                        "status", 404,
                        "error", "Bank Balance Not Found",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(TransactionNotFound.class)
    public ResponseEntity<Map<String,Object>> transactionNotFoundExceptionHandler(
            TransactionNotFound ex) {
        return ResponseEntity.status(404)
                .body(Map.of(
                        "timestamp", java.time.LocalDateTime.now(),
                        "status", 404,
                        "error", "Transaction Not Found",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(QrNotFoundException.class)
    public ResponseEntity<Map<String,Object>> QrNotFoundException(
            QrNotFoundException ex) {
        return ResponseEntity.status(404)
                .body(Map.of(
                        "timestamp", java.time.LocalDateTime.now(),
                        "status", 404,
                        "error", "Qr Not Found",
                        "message", ex.getMessage()
                ));
    }


}
