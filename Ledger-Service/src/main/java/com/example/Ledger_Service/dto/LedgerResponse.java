package com.example.Ledger_Service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LedgerResponse {

    private Long ledgerId;

    private String transactionRefNo;

    private Long userId;

    private String entryType;

    private BigDecimal amount;

    private BigDecimal balanceBefore;

    private BigDecimal balanceAfter;
}
