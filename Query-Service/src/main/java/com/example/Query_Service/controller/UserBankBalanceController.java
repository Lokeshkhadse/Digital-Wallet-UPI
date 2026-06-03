package com.example.Query_Service.controller;

import com.example.Query_Service.dto.GenericResponse;
import com.example.Query_Service.dto.UserBankBalanceResponse;
import com.example.Query_Service.service.UserBankBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/query/user-bank-balance")
@RequiredArgsConstructor
public class UserBankBalanceController {

    private final UserBankBalanceService service;

    @GetMapping("/getByUserId/{userId}")
    public ResponseEntity<GenericResponse<UserBankBalanceResponse>> getBalanceByUserId(
            @PathVariable Long userId) {
        UserBankBalanceResponse response = service.getBalanceByUserId(userId);

        return ResponseEntity.ok(new GenericResponse<>("Balance retrieved successfully", response, 200));
    }

         @GetMapping("/getByBankId/{userBankId}")
    public ResponseEntity<GenericResponse<UserBankBalanceResponse>> getBalanceByBankId( @PathVariable Long userBankId) {
        UserBankBalanceResponse response = service.getBalanceByBankId(userBankId);

        return ResponseEntity.ok(new GenericResponse<>("Balance retrieved successfully", response, 200));

     }

    @GetMapping("/getAllBankBalances")
    public Page<UserBankBalanceResponse> getAllBankBalances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return service.getAllBankBalances(page, size);
    }

    @GetMapping("/low")
    public Page<UserBankBalanceResponse> lowBalanceAccounts(
            @RequestParam BigDecimal threshold,
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "10")
            int size) {

        return service.getLowBalanceAccounts(
                threshold,
                page,
                size);
    }
}
