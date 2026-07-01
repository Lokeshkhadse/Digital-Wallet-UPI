package com.example.Action_Service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateSplitParticipantRequest {

    private Long userId;

    private Long userBankId;

    private BigDecimal shareAmount;
}