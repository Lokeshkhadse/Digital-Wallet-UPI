package com.example.Action_Service.dto;

import lombok.Data;

@Data
public class QrLookupResponse {

    private Long userId;

    private Long userBankId;

    private String upiId;

    private String bankName;

    private String accountNumber;

    private String accountHolderName;
}