package com.example.Action_Service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferTransactionRequest {

    private Long senderUserId;
    private Long receiverUserId;

    private Long senderBankId;
    private Long receiverBankId;
    private String upiPin;


    @NotNull
    @DecimalMin(value = "1.00", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank
    private String transactionType;

    private String remarks;

}