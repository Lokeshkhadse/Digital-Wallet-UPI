package com.example.Kyc_Service.dto;


import com.example.Kyc_Service.enums.KycStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KycStatusResponseDto {

    private Long kycId;
    private Long userId;
    private KycStatus status;
    private String rejectionReason;
}