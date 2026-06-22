package com.example.Query_Service.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SettlementSummaryResponse {

    private Long splitBillId;

    private BigDecimal totalAmount;

    private BigDecimal paidAmount;

    private BigDecimal remainingAmount;

    private Integer totalParticipants;

    private Integer paidParticipants;

    private Integer pendingParticipants;
}