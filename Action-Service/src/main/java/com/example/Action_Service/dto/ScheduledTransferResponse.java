package com.example.Action_Service.dto;

import com.example.Action_Service.enums.ScheduledTransferFrequency;
import com.example.Action_Service.enums.ScheduleStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ScheduledTransferResponse {

    private Long id;

    private Long senderUserId;

    private Long receiverUserId;

    private BigDecimal amount;

    private ScheduledTransferFrequency frequency;

    private ScheduleStatus status;

    private LocalDateTime nextExecutionTime;
}