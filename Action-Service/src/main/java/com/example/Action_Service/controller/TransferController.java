package com.example.Action_Service.controller;

import com.example.Action_Service.dto.GenericResponse;
import com.example.Action_Service.dto.TransferTransactionRequest;
import com.example.Action_Service.dto.TransferTransactionResponse;
import com.example.Action_Service.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/action/transfer")
public class TransferController {
    private final TransferService transferService;

    @PostMapping("/amount")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GenericResponse<TransferTransactionResponse>> transfer(
            @Valid @RequestBody TransferTransactionRequest request) {

        TransferTransactionResponse response = transferService.transfer(request);

        return ResponseEntity.ok(new GenericResponse<>("Transaction created successfully", response, 200));

    }
}

