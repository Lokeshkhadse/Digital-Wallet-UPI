package com.example.Action_Service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBankBalanceRequest {

    @NotNull
    private Long userBankId;

    @NotNull
    private Long userId;

    @NotNull
    private BigDecimal availableBalance;
}
