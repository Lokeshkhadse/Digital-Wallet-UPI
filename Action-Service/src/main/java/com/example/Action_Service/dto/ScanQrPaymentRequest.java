package com.example.Action_Service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ScanQrPaymentRequest {

    private Long senderUserId;

    private Long senderBankId;

    private String upiPin;

    private String upiId;

    private BigDecimal amount;

    private String remarks;
}