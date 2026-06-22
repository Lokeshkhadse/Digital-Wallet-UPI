package com.example.Action_Service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class UserBankBalanceResponse {

    private Long id;

    private Long userBankId;

    private Long userId;

    private BigDecimal availableBalance;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
