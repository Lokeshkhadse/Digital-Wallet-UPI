package com.example.Query_Service.controller;

import com.example.Query_Service.dto.TransferTransactionResponse;
import com.example.Query_Service.enums.TransactionStatus;
import com.example.Query_Service.service.DepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/query/deposit")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService service;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<TransferTransactionResponse> getUserDeposits(

            @PathVariable Long userId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return service.getUserDeposits(
                userId,
                page,
                size);
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<TransferTransactionResponse> getByStatus(

            @RequestParam TransactionStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return service.getDepositsByStatus(
                status,
                page,
                size);
    }

    @GetMapping("/amount")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<TransferTransactionResponse> getByAmountRange(

            @RequestParam BigDecimal min,

            @RequestParam BigDecimal max,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return service.getDepositsByAmountRange(
                min,
                max,
                page,
                size);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<TransferTransactionResponse> getByDateRange(

            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
            LocalDateTime from,

            @RequestParam
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
            LocalDateTime to,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return service.getDepositsByDateRange(
                from,
                to,
                page,
                size);
    }
}