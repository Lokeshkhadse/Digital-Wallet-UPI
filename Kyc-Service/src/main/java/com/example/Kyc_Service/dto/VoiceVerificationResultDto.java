package com.example.Kyc_Service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VoiceVerificationResultDto {

    private Long userId;

    private Double nameMatchPercentage;

    private boolean dobMatched;

    private boolean panMatched;

    private boolean aadhaarMatched;

    private String finalStatus;

    private String remarks;
}