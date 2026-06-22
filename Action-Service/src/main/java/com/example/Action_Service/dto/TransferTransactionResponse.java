package com.example.Action_Service.dto;

import com.example.Action_Service.enums.TransactionType;
import lombok.Builder;
import lombok.Data;


import java.math.BigDecimal;


@Data
@Builder
public class TransferTransactionResponse {

    private String transactionRefNo;

    private String status;

    private BigDecimal amount;

    private TransactionType transactionType;

    private String message;
}
