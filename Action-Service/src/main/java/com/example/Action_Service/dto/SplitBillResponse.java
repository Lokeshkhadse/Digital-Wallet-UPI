package com.example.Action_Service.dto;

import com.example.Action_Service.enums.SplitBillStatus;
import com.example.Action_Service.enums.SplitType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SplitBillResponse {

    private Long splitBillId;

    private Long createdByUserId;

    private String title;

    private String description;

    private BigDecimal totalAmount;

    private BigDecimal paidAmount;

    private BigDecimal remainingAmount;

    private SplitType splitType;

    private SplitBillStatus status;

    private LocalDateTime dueDate;

    private LocalDateTime expiryDate;

    private LocalDateTime createdAt;

    private List<SplitBillParticipantResponse> participants;
}