package com.example.Action_Service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
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


    @ExceptionHandler(AuthServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleAuthDown(AuthServiceUnavailableException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "timestamp", java.time.LocalDateTime.now(),
                        "status", 503,
                        "error", "Auth Service Unavailable",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "timestamp", java.time.LocalDateTime.now(),
                        "status", 500,
                        "error", "Internal Server Error",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(LedgerServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> handleLedgerDown(
            LedgerServiceUnavailableException ex) {

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 503,
                        "error", "Ledger Service Unavailable",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(InvalidBankOwnerException.class)
    public ResponseEntity<Map<String, Object>> InvalidBankOwnerException(
            InvalidBankOwnerException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 503,
                        "error", "Invalid Bank Owner",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<Map<String, Object>> InvalidAmountException(
            InvalidAmountException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "status", 503,
                        "error", "Invalid Amount ,check the Amount",
                        "message", ex.getMessage()
                ));
    }
}
