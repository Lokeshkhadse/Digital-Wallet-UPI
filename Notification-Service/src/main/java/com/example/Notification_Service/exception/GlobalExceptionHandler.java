package com.example.Notification_Service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FailedToSendEmail.class)
    public ResponseEntity<Map<String,Object>> failedToSendMail(FailedToSendEmail ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("timestamp", java.time.LocalDateTime.now(),
                        "status", 404,
                        "error", "Failed To Send Mail",
                        "message", ex.getMessage()
                ));
    }
}
