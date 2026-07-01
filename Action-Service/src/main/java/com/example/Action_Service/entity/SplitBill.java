package com.example.Action_Service.entity;

import com.example.Action_Service.enums.SplitBillStatus;
import com.example.Action_Service.enums.SplitType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "split_bills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SplitBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long splitBillId;

    private Long createdByUserId;

    private Long receiverBankId;

    private String title;

    private String description;

    private BigDecimal totalAmount;

    private BigDecimal paidAmount;

    private BigDecimal remainingAmount;

    @Enumerated(EnumType.STRING)
    private SplitType splitType;

    @Enumerated(EnumType.STRING)
    private SplitBillStatus status;

    private LocalDateTime dueDate;

    private LocalDateTime expiryDate;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}