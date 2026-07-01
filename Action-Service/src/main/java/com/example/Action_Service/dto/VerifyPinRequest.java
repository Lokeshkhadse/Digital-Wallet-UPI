package com.example.Action_Service.dto;

import lombok.Data;

@Data
public class VerifyPinRequest {

    private Long userId;

    private Long userBankId;

    private String pin;
}