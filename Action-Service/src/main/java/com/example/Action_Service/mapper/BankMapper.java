package com.example.Action_Service.mapper;


import com.example.Action_Service.dto.BankRequest;
import com.example.Action_Service.dto.BankResponse;
import com.example.Action_Service.entity.UserBankDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BankMapper {

    // 🔥 DTO → ENTITY
    public UserBankDetails toEntity(BankRequest request) {

        if (request == null) return null;

        UserBankDetails bank = new UserBankDetails();

        bank.setUserId(request.getUserId());
        bank.setAccountNumber(request.getAccountNumber());
        bank.setIfscCode(request.getIfscCode());
        bank.setBankName(request.getBankName());
        bank.setUpiId(request.getUpiId());
        bank.setAccountType(request.getAccountType());
        bank.setCreatedAt(LocalDateTime.now());

        return bank;
    }

    // 🔥 ENTITY → DTO (Response)
    public BankResponse toResponse(UserBankDetails bank) {

        if (bank == null) return null;

        return BankResponse.builder()
                .id(bank.getId())
                .userId(bank.getUserId())
                .accountNumber(bank.getAccountNumber())
                .ifscCode(bank.getIfscCode())
                .bankName(bank.getBankName())
                .upiId(bank.getUpiId())
                .accountType(bank.getAccountType())
                .createdAt(bank.getCreatedAt())
                .build();
    }
}