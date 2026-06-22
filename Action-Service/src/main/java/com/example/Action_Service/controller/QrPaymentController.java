package com.example.Action_Service.controller;

import com.example.Action_Service.dto.*;
import com.example.Action_Service.service.QrPaymentService;
import jakarta.validation.Valid; // Ensure you have this dependency
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/action/qr")
@RequiredArgsConstructor
public class QrPaymentController {

    private final QrPaymentService service;

    @PostMapping("/pay")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GenericResponse<TransferTransactionResponse>> payUsingQr(
            @Valid @RequestBody ScanQrPaymentRequest request) {

        TransferTransactionResponse data = service.payUsingQr(request);

        return ResponseEntity.ok(new GenericResponse<>("QR Payment Successful", data, 200));

    }


//    @PostMapping("/generate")
//    public ResponseEntity<GenerateQrResponse>
//    generateQr(
//            @RequestBody
//            GenerateQrRequest request){
//
//        return ResponseEntity.ok(
//                service.generateQr(request));
//    }
}