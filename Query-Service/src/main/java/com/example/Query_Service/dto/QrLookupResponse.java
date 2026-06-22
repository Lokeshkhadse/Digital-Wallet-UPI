package com.example.Query_Service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QrLookupResponse {

    private Long userId;

    private Long userBankId;

    private String upiId;

    private String bankName;

    private String accountNumber;

    private String accountHolderName;
}