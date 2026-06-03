package com.example.Action_Service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long userBankId;

    @DecimalMin("1.00")
    private BigDecimal amount;

    private String remarks;
}
