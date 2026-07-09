package com.example.Query_Service.controller;

import com.example.Query_Service.dto.GenericResponse;
import com.example.Query_Service.dto.UserBankBalanceResponse;
import com.example.Query_Service.service.UserBankBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/query/user-bank-balance")
@RequiredArgsConstructor
public class UserBankBalanceController {

    private final UserBankBalanceService service;

    @GetMapping("/getByUserId/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<GenericResponse<List<UserBankBalanceResponse>>> getBalanceByUserId(
            @PathVariable Long userId) {
        List<UserBankBalanceResponse> response = service.getBalanceByUserId(userId);

        return ResponseEntity.ok(new GenericResponse<>("Balance retrieved successfully", response, 200));
    }

         @GetMapping("/getByBankId/{userBankId}")
         @PreAuthorize("hasAnyRole('ADMIN','USER')")
         public ResponseEntity<GenericResponse<UserBankBalanceResponse>> getBalanceByBankId( @PathVariable Long userBankId) {
        UserBankBalanceResponse response = service.getBalanceByBankId(userBankId);

        return ResponseEntity.ok(new GenericResponse<>("Balance retrieved successfully", response, 200));

     }

    @GetMapping("/getAllBankBalances")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserBankBalanceResponse> getAllBankBalances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return service.getAllBankBalances(page, size);
    }

    @GetMapping("/low")
    @PreAuthorize("hasRole('ADMIN')")
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
