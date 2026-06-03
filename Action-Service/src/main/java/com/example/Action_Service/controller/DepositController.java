package com.example.Action_Service.controller;

import com.example.Action_Service.dto.DepositRequest;
import com.example.Action_Service.dto.GenericResponse;
import com.example.Action_Service.dto.TransferTransactionResponse;
import com.example.Action_Service.service.DepositService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/action/deposit")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    @PostMapping("/amount")
    public ResponseEntity<GenericResponse<TransferTransactionResponse>>
    deposit(
            @Valid
            @RequestBody DepositRequest request) {

        TransferTransactionResponse response =
                depositService.deposit(request);

        return ResponseEntity.ok(new GenericResponse<>("Transaction Deposit created successfully", response, 200));

    }
}