package com.example.Query_Service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDashboardResponse {

    private Long bankId;

    private String bankName;

    private String accountNumber;

    private String upiId;

    private BigDecimal balance;

    private Long totalTransactions;

    private Long todayTransactions;

    private BigDecimal totalCredit;

    private BigDecimal totalDebit;

    private TransferTransactionResponse lastTransaction;

    private List<TransferTransactionResponse> recentTransactions;
}