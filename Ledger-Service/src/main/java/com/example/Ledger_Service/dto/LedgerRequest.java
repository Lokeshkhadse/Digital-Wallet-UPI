package com.example.Ledger_Service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LedgerRequest {

    private String transactionRefNo;

    private Long userId;

    private Long userBankId;

    private String entryType;

    private BigDecimal amount;

    private BigDecimal balanceBefore;

    private BigDecimal balanceAfter;

    private String remarks;
}
