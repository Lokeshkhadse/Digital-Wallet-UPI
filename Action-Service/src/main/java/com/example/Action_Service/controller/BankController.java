package com.example.Action_Service.controller;

import com.example.Action_Service.dto.BankRequest;
import com.example.Action_Service.dto.BankResponse;
import com.example.Action_Service.dto.GenericResponse;
import com.example.Action_Service.service.BankService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/action/bank")
@AllArgsConstructor
public class BankController {

    private final BankService bankService;

    @PostMapping("/add-bank-details")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenericResponse<BankResponse>> addBankDetails(@Valid  @RequestBody BankRequest bankRequest) {
        BankResponse response = bankService.addBankDetails(bankRequest);
        return ResponseEntity.ok(new GenericResponse<>("Bank details added successfully", response, 200));
    }



    @DeleteMapping("/delete/{bankId}")
    public ResponseEntity<GenericResponse<String>> deleteBank(
            @PathVariable Long bankId) {

        String response = bankService.deleteBank(bankId);

        return ResponseEntity.ok(
                new GenericResponse<>("Bank deleted successfully", response, 200)
        );
    }

}
