package com.example.Api_Gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback/auth")
    public ResponseEntity<Map<String, Object>> authFallback() {
        return buildResponse("Auth Service is currently unavailable");
    }

    @GetMapping("/fallback/action")
    public ResponseEntity<Map<String, Object>> actionFallback() {
        return buildResponse("Action Service is currently unavailable");
    }

    @GetMapping("/fallback/query")
    public ResponseEntity<Map<String, Object>> queryFallback() {
        return buildResponse("Query Service is currently unavailable");
    }

    @GetMapping("/fallback/ledger")
    public ResponseEntity<Map<String, Object>> ledgerFallback() {
        return buildResponse("Ledger Service is currently unavailable");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(String message) {

        Map<String, Object> response = new HashMap<>();

        response.put("status", "FAILURE");
        response.put("message", message);
        response.put("code", 503);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}