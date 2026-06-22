package com.example.Action_Service.entity;

import com.example.Action_Service.enums.ScheduledTransferFrequency;
import com.example.Action_Service.enums.ScheduleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderUserId;

    private Long receiverUserId;

    private Long senderBankId;

    private Long receiverBankId;
    private String upiPin;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private ScheduledTransferFrequency frequency;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;

    private LocalDateTime nextExecutionTime;

    private String remarks;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}