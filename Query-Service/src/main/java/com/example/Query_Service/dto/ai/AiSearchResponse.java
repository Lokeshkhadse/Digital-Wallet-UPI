package com.example.Query_Service.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiSearchResponse {

    private AiIntent intent;

    // Transaction
    private String transactionType;
    private String status;
    private String referenceNumber;

    // Date
    private String fromDate;
    private String toDate;

    // Amount
    private String minAmount;
    private String maxAmount;

    // Pagination / Limit
    private Integer limit;

    // User Inputs
    private Long bankId;

    private String bankName;

    private String ifsc;

    private String accountNumber;

    private String accountType;

    private String originalQuestion;
}