package com.example.Query_Service.controller;


import com.example.Query_Service.dto.BeneficiaryResponse;
import com.example.Query_Service.service.BeneficiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/query/beneficiary")
@RequiredArgsConstructor
public class BeneficiaryController {

    private final BeneficiaryService service;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<BeneficiaryResponse>
    getUserBeneficiaries(
            @PathVariable Long userId) {

        return service.getUserBeneficiaries(userId);
    }

    @GetMapping("/{beneficiaryId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public BeneficiaryResponse
    getBeneficiary(
            @PathVariable Long beneficiaryId) {

        return service.getBeneficiary(
                beneficiaryId);
    }
}