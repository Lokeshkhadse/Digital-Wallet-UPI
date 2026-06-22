package com.example.Query_Service.mapper;

import com.example.Query_Service.dto.UserBankDetailsResponse;
import com.example.Query_Service.entity.UserBankDetails;
import org.springframework.stereotype.Component;

@Component
public class UserBankDetailsMapper {

    public UserBankDetailsResponse toResponse(
            UserBankDetails entity) {

        return UserBankDetailsResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .bankName(entity.getBankName())
                .accountNumber(entity.getAccountNumber())
                .ifscCode(entity.getIfscCode())
                .upiId(entity.getUpiId())
                .accountType(entity.getAccountType())
                .build();
    }
}