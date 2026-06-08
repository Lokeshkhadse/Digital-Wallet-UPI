package com.example.Query_Service.controller;

import com.example.Query_Service.dto.TransferTransactionResponse;
import com.example.Query_Service.enums.TransactionStatus;
import com.example.Query_Service.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/query/withdraw")
@RequiredArgsConstructor
public class WithdrawController {

    private final WithdrawService service;

    @GetMapping("/user/{receiverUserId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<TransferTransactionResponse> getUserWithdrawals(

            @PathVariable Long receiverUserId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return service.getUserWithdrawals(
                receiverUserId,
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

        return service.getWithdrawalsByStatus(
                status,
                page,
                size);
    }

    @GetMapping("/amount")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<TransferTransactionResponse> getByAmount(

            @RequestParam BigDecimal min,

            @RequestParam BigDecimal max,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return service.getWithdrawalsByAmount(
                min,
                max,
                page,
                size);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<TransferTransactionResponse> getByDate(

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

        return service.getWithdrawalsByDate(
                from,
                to,
                page,
                size);
    }

    @GetMapping("/mini-statement/{senderUserId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public List<TransferTransactionResponse> miniStatement(
            @PathVariable Long senderUserId) {

        return service.getMiniStatement(senderUserId);
    }
}