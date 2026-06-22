package com.example.Kyc_Service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KycVerificationResponseDto {

    private Long userId;

    private Double panMatchPercentage;

    private Double aadhaarMatchPercentage;

    private Double panAadhaarMatchPercentage;

    private boolean panDobMatched;

    private boolean aadhaarDobMatched;

    private boolean panAadhaarDobMatched;

    private String finalStatus;

    private String remarks;
}