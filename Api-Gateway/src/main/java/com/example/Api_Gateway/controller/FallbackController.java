//package com.example.Api_Gateway.controller;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/fallback")
//public class FallbackController {
//
//    @RequestMapping("/auth")
//    public ResponseEntity<Map<String, Object>> authFallback() {
//        return buildResponse("Auth Service is currently unavailable");
//    }
//
//    @RequestMapping("/action")
//    public ResponseEntity<Map<String, Object>> actionFallback() {
//        return buildResponse("Action Service is currently unavailable");
//    }
//
//    @RequestMapping("/query")
//    public ResponseEntity<Map<String, Object>> queryFallback() {
//        return buildResponse("Query Service is currently unavailable");
//    }
//
//    @RequestMapping("/ledger")
//    public ResponseEntity<Map<String, Object>> ledgerFallback() {
//        return buildResponse("Ledger Service is currently unavailable");
//    }
//
//    private ResponseEntity<Map<String, Object>> buildResponse(String message) {
//
//        Map<String, Object> response = new HashMap<>();
//
//        response.put("timestamp", LocalDateTime.now());
//        response.put("status", "FAILURE");
//        response.put("code", 503);
//        response.put("message", message);
//
//        return ResponseEntity
//                .status(HttpStatus.SERVICE_UNAVAILABLE)
//                .body(response);
//    }
//}