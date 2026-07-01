package com.example.Action_Service.dto;

import lombok.Data;

@Data
public class PaySplitBillRequest {

    private Long splitBillId;

    private Long participantUserId;

    private Long participantBankId;

    private String upiPin;
}