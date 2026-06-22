package com.example.Action_Service.controller;

import com.example.Action_Service.dto.GenericResponse;
import com.example.Action_Service.dto.TransferTransactionResponse;
import com.example.Action_Service.dto.WithdrawRequest;
import com.example.Action_Service.service.WithdrawService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/action/withdraw")
@RequiredArgsConstructor
public class WithdrawController {

    private final WithdrawService withdrawService;

    @PostMapping("/amount")
//    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GenericResponse<TransferTransactionResponse>>
    withdraw(
            @Valid
            @RequestBody WithdrawRequest request) {

        TransferTransactionResponse response =
                withdrawService.withdraw(request);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Withdraw completed successfully",
                        response,
                        200
                )
        );
    }
}