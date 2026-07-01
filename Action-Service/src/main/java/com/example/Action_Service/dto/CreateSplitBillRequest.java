package com.example.Action_Service.dto;

import com.example.Action_Service.enums.SplitType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateSplitBillRequest {

    private Long createdByUserId;
    private Long receiverBankId;

    private String title;

    private String description;

    private BigDecimal totalAmount;

    private SplitType splitType;

    private LocalDateTime dueDate;

    private LocalDateTime expiryDate;

    private List<CreateSplitParticipantRequest> participants;
}