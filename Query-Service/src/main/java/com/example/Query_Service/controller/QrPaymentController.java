package com.example.Query_Service.controller;

import com.example.Query_Service.dto.*;
import com.example.Query_Service.service.QrPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/query/qr")
@RequiredArgsConstructor
public class QrPaymentController {

    private final QrPaymentService service;


    @GetMapping("/lookup/{upiId}")
    public ResponseEntity<QrLookupResponse>
    lookupQr(
            @PathVariable String upiId){

        return ResponseEntity.ok(
                service.lookupQr(upiId));
    }
}