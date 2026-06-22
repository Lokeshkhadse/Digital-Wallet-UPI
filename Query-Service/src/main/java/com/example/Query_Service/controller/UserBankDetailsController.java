package com.example.Query_Service.controller;

import com.example.Query_Service.dto.GenericResponse;
import com.example.Query_Service.dto.UserBankDetailsResponse;
import com.example.Query_Service.enums.AccountType;
import com.example.Query_Service.service.UserBankDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/query/user-bank-details")
@RequiredArgsConstructor
public class UserBankDetailsController {

    private final UserBankDetailsService service;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<GenericResponse<List<UserBankDetailsResponse>>> getBanksByUserId(
            @PathVariable Long userId) {

        List<UserBankDetailsResponse> banks = service.getBanksByUserId(userId);
        return new ResponseEntity<>(new GenericResponse<>("Bank details fetched successfully", banks, 200), HttpStatus.OK);
    }

    @GetMapping("/bank/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<GenericResponse<UserBankDetailsResponse>> getBankById(
            @PathVariable Long id) {

        UserBankDetailsResponse bank = service.getBankById(id);
       return new ResponseEntity<>(new GenericResponse<>("Bank details fetched successfully", bank, 200), HttpStatus.OK);
    }

    @GetMapping("/search/account-number")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<GenericResponse<UserBankDetailsResponse>> searchByAccountNumber(
            @RequestParam String accountNumber) {
        UserBankDetailsResponse userBankDetailsResponse = service.searchByAccountNumber(accountNumber);
        return new ResponseEntity<>(new GenericResponse<>("Bank details fetched successfully", userBankDetailsResponse, 200), HttpStatus.OK);
    }

    @GetMapping("/getAllBanks")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserBankDetailsResponse> getAllBanks(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return service.getAllBanks(page, size);
    }

    @GetMapping("/search/bank-name")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<UserBankDetailsResponse> searchByBankName(

            @RequestParam String bankName,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return service.searchByBankName(
                bankName,
                page,
                size);
    }

    @GetMapping("/search/ifsc")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserBankDetailsResponse> searchByIfsc(

            @RequestParam String ifsc,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return service.searchByIfsc(
                ifsc,
                page,
                size);
    }

    @GetMapping("/search/account-type")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserBankDetailsResponse> searchByAccountType(

            @RequestParam AccountType accountType,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return service.searchByAccountType(
                accountType,
                page,
                size);
    }
}