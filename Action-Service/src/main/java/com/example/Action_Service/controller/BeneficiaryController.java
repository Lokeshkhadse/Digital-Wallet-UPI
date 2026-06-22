package com.example.Action_Service.controller;

import com.example.Action_Service.dto.BeneficiaryRequest;
import com.example.Action_Service.dto.BeneficiaryResponse;
import com.example.Action_Service.dto.GenericResponse;
import com.example.Action_Service.service.BeneficiaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/action/beneficiary")
@RequiredArgsConstructor
public class BeneficiaryController {

    private final BeneficiaryService service;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<GenericResponse<BeneficiaryResponse>>
    addBeneficiary(
            @Valid
            @RequestBody BeneficiaryRequest request) {

        BeneficiaryResponse response =
                service.addBeneficiary(request);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Beneficiary added successfully",
                        response,
                        200
                )
        );
    }

    @PutMapping("/update/{beneficiaryId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<GenericResponse<BeneficiaryResponse>>
    updateBeneficiary(
            @PathVariable Long beneficiaryId,
            @RequestBody BeneficiaryRequest request) {

        BeneficiaryResponse response =
                service.updateBeneficiary(
                        beneficiaryId,
                        request);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Beneficiary updated successfully",
                        response,
                        200
                )
        );
    }

    @DeleteMapping("/delete/{beneficiaryId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<GenericResponse<String>>
    deleteBeneficiary(
            @PathVariable Long beneficiaryId) {

        String response =
                service.deleteBeneficiary(
                        beneficiaryId);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        response,
                        response,
                        200
                )
        );
    }
}