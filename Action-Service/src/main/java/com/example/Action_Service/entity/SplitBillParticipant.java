package com.example.Action_Service.entity;

import com.example.Action_Service.enums.SplitPaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "split_bill_participants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SplitBillParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId;

    private Long splitBillId;

    private Long userId;

    private Long userBankId;

    private BigDecimal shareAmount;

    private BigDecimal paidAmount;

    private BigDecimal remainingAmount;

    @Enumerated(EnumType.STRING)
    private SplitPaymentStatus paymentStatus;

    private String transactionRefNo;

    private LocalDateTime paidAt;
}