package com.example.Action_Service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserBankBalanceRequest {

    @NotNull
    private Long userBankId;

    @NotNull
    private Long userId;

    @NotNull
    private BigDecimal availableBalance;
}
