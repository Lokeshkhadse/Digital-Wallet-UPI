package com.example.Ledger_Service.controller;

import com.example.Ledger_Service.dto.LedgerRequest;
import com.example.Ledger_Service.dto.LedgerResponse;
import com.example.Ledger_Service.service.LedgerService;
import jakarta.annotation.PreDestroy;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ledger")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Map<String, Object>> createLedgerEntry(
            @Valid @RequestBody LedgerRequest request) {

        LedgerResponse response = ledgerService.createEntry(request);

        return new ResponseEntity<>(
                Map.of(
                        "status", "success",
                        "data", response
                ),
                HttpStatus.CREATED
        );
    }


    @GetMapping("/getById/{ledgerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getLedgerById(@PathVariable Long ledgerId) {
        LedgerResponse response = ledgerService.getLedgerById(ledgerId);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response
        ));
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllLedgers() {
        List<LedgerResponse> response = ledgerService.getAllLedgers();

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", response
        ));
    }
}