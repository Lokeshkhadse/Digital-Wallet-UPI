package com.example.Query_Service.dto;

import com.example.Query_Service.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
public class TransferTransactionResponse {

    private Long transactionId;

    private String transactionRefNo;

    private Long senderUserId;

    private Long receiverUserId;

    private BigDecimal amount;

    private String transactionType;

    private String transactionStatus;

    private String remarks;

    private LocalDateTime createdAt;
}
