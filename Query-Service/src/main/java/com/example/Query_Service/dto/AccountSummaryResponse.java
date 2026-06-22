package com.example.Query_Service.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSummaryResponse {

    private Long bankId;

    private String bankName;

    private String accountNumber;

    private String upiId;

    private BigDecimal balance;
}