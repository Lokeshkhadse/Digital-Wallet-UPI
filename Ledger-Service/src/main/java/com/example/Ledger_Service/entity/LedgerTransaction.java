package com.example.Ledger_Service.entity;


import com.example.Ledger_Service.enums.LedgerEntryType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LedgerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ledgerId;

    private String transactionRefNo;

    private Long userId;

    private Long userBankId;

    @Enumerated(EnumType.STRING)
    private LedgerEntryType entryType;

    private BigDecimal amount;

    private BigDecimal balanceBefore;

    private BigDecimal balanceAfter;

    private String remarks;

    @CreatedDate
    private LocalDateTime createdAt;
}
