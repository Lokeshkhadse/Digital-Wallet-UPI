package com.example.Action_Service.mapper;

import com.example.Action_Service.dto.UserBankBalanceRequest;
import com.example.Action_Service.dto.UserBankBalanceResponse;
import com.example.Action_Service.entity.UserBankBalance;
import org.springframework.stereotype.Component;

@Component
public class UserBankBalanceMapper {

    //request to entity
    public UserBankBalance toEntity(UserBankBalanceRequest request) {
        return UserBankBalance.builder()
                .userBankId(request.getUserBankId())
                .userId(request.getUserId())
                .availableBalance(request.getAvailableBalance())
                .build();
    }

    //entity to response
    public UserBankBalanceResponse toResponse(UserBankBalance balance) {
        return UserBankBalanceResponse.builder()
                .id(balance.getUserBankBalanceId())
                .userBankId(balance.getUserBankId())
                .userId(balance.getUserId())
                .availableBalance(balance.getAvailableBalance())
                .createdAt(balance.getCreatedAt())
                .updatedAt(balance.getUpdatedAt())
                .build();
    }
}