package com.example.Query_Service.mapper;

import com.example.Query_Service.dto.UserBankBalanceResponse;
import com.example.Query_Service.entity.UserBankBalance;
import com.example.Query_Service.service.UserBankBalanceService;
import org.springframework.stereotype.Component;

@Component
public class UserBankBalanceMapper {

    public UserBankBalanceResponse toResponse(UserBankBalance userBankBalance){
        return UserBankBalanceResponse.builder()
                .userBankBalanceId(userBankBalance.getUserBankBalanceId())
                .userId(userBankBalance.getUserId())
                .userBankId(userBankBalance.getUserBankId())
                .availableBalance(userBankBalance.getAvailableBalance())
                .createdAt(userBankBalance.getCreatedAt())
                .updatedAt(userBankBalance.getUpdatedAt())
                .build();
    }
}
