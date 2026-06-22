package com.example.Query_Service.controller;

import com.example.Query_Service.dto.TransferTransactionResponse;
import com.example.Query_Service.enums.TransactionStatus;
import com.example.Query_Service.enums.TransactionType;
import com.example.Query_Service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
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


    @GetMapping("/user/{senderUserId}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<InputStreamResource>
    downloadTransactionPdf(
            @PathVariable Long senderUserId) {

        ByteArrayInputStream pdf =
                service.downloadTransactionPdf(
                        senderUserId);

        HttpHeaders headers =
                new HttpHeaders();

        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=transaction-report.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(
                        MediaType.APPLICATION_PDF)
                .body(
                        new InputStreamResource(pdf));
    }

    @GetMapping("/mini-statement/{senderUserId}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<InputStreamResource>
    downloadMiniStatementPdf(
            @PathVariable Long senderUserId) {

        ByteArrayInputStream pdf =
                service.miniStatementPdf(
                        senderUserId);

        HttpHeaders headers =
                new HttpHeaders();

        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=mini-statement.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(
                        MediaType.APPLICATION_PDF)
                .body(
                        new InputStreamResource(pdf));
    }



}