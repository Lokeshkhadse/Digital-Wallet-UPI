package com.example.Action_Service.dto;

import com.example.Action_Service.enums.SplitPaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class SplitBillParticipantResponse {

    private Long participantId;

    private Long userId;

    private Long userBankId;

    private BigDecimal shareAmount;

    private BigDecimal paidAmount;

    private BigDecimal remainingAmount;

    private SplitPaymentStatus paymentStatus;

    private String transactionRefNo;

    private LocalDateTime paidAt;
}