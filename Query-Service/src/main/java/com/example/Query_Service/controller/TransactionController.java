package com.example.Query_Service.controller;

import com.example.Query_Service.dto.TransferTransactionResponse;
import com.example.Query_Service.enums.TransactionStatus;
import com.example.Query_Service.enums.TransactionType;
import com.example.Query_Service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/query/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    @GetMapping("/user/{senderUserId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<TransferTransactionResponse> getUserTransactions(

            @PathVariable Long senderUserId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return service.getUserTransactions(
                senderUserId,
                page,
                size);
    }

    @GetMapping("/getByRefNo/{transactionRefNo}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public TransferTransactionResponse getByRefNo(
            @PathVariable String transactionRefNo) {

        return service.getByRefNo(transactionRefNo);
    }

    @GetMapping("/mini-statement/{senderUserId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public List<TransferTransactionResponse> miniStatement(
            @PathVariable Long senderUserId) {

        return service.miniStatement(senderUserId);
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<TransferTransactionResponse> getByStatus(

            @RequestParam TransactionStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return service.getByStatus(
                status,
                page,
                size);
    }

    @GetMapping("/type")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Page<TransferTransactionResponse> getByType(

            @RequestParam TransactionType type,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return service.getByType(
                type,
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

        return service.getByDateRange(
                from,
                to,
                page,
                size);
    }
}