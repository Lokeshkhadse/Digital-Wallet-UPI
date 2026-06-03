package com.example.Auth_Service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "status", 404,
                        "error", "EMAIL_ERROR",
                        "message", ex.getMessage()
                ));
    }


    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<Map<String,Object>> handleEmailAlreadyExistException(EmailAlreadyExistException ex) {
        return new ResponseEntity<>(Map.of(
                "status", 409,
                "error", "EMAIL_ALREADY_EXIST",
                "message", ex.getMessage()
        ), HttpStatus.CONFLICT);

    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "status", 401,
                        "error", "PASSWORD_ERROR",
                        "message", ex.getMessage()
                ));
    }

}
