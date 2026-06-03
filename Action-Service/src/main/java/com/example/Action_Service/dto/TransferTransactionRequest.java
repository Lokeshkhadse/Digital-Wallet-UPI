package com.example.Action_Service.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferTransactionRequest {

    private Long senderUserId;
    private Long receiverUserId;

    private Long senderBankId;
    private Long receiverBankId;

    @NotBlank
    private String idempotencyKey;

    @NotNull
    @DecimalMin(value = "1.00", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank
    private String transactionType;

    private String remarks;

}