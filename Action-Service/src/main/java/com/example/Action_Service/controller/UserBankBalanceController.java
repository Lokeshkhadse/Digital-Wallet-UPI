package com.example.Action_Service.controller;

import com.example.Action_Service.dto.GenericResponse;
import com.example.Action_Service.dto.UserBankBalanceRequest;
import com.example.Action_Service.dto.UserBankBalanceResponse;
import com.example.Action_Service.service.UserBankBalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/action/user-bank-balance")
@RequiredArgsConstructor
public class UserBankBalanceController {

    private final UserBankBalanceService service;

    @PostMapping("/create")
    public ResponseEntity<GenericResponse<UserBankBalanceResponse>> createBalance(
            @Valid @RequestBody UserBankBalanceRequest request) {
        UserBankBalanceResponse response = service.createBalance(request);

        return ResponseEntity.ok(new GenericResponse<>("Balance created successfully", response, 200));


    }

    @PatchMapping("/update/{userBankBalanceId}")
    public ResponseEntity<GenericResponse<UserBankBalanceResponse>> updateBalance(
            @PathVariable Long userBankBalanceId,
            @RequestBody UserBankBalanceRequest request) {

        UserBankBalanceResponse response = service.updateBalance(userBankBalanceId, request);

        return ResponseEntity.ok(
                new GenericResponse<>("Balance updated successfully", response, 200)
        );
    }

    @DeleteMapping("/delete/{balanceId}")
    public ResponseEntity<Map<String, Object>> deleteBalance(
            @PathVariable Long userBankBalanceId) {

        String response =
                service.deleteBalance(userBankBalanceId);

        Map<String, Object> result = new HashMap<>();

        result.put("status", "SUCCESS");
        result.put("message", response);

        return ResponseEntity.ok(result);
    }


}