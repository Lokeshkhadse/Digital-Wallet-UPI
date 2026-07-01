package com.example.Query_Service.dto;

import com.example.Query_Service.enums.TransactionStatus;
import com.example.Query_Service.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TransactionFilterRequest {

    private Long userId;

    private TransactionType transactionType;

    private TransactionStatus transactionStatus;

    private LocalDate fromDate;

    private LocalDate toDate;

    private Integer page = 0;

    private Integer size = 10;
}
