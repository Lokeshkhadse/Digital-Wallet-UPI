package com.example.Query_Service.dto;

import com.example.Query_Service.enums.AccountType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BankResponse {

    private Long id;
    private Long userId;
    private String accountNumber;
    private String ifscCode;
    private String bankName;
    private String upiId;
    private AccountType accountType;
    private LocalDateTime createdAt;
}
