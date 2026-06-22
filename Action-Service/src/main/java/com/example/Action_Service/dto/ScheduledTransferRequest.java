package com.example.Action_Service.dto;

import com.example.Action_Service.enums.ScheduledTransferFrequency;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScheduledTransferRequest {

    @NotNull
    private Long senderUserId;

    @NotNull
    private Long receiverUserId;

    @NotNull
    private Long senderBankId;

    @NotNull
    private Long receiverBankId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private ScheduledTransferFrequency frequency;

    @NotNull
    private LocalDateTime nextExecutionTime;

    private String upiPin;

    private String remarks;
}