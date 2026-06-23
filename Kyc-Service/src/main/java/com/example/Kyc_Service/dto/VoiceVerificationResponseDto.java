package com.example.Kyc_Service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VoiceVerificationResponseDto {

    private Long voiceVerificationId;

    private Long userId;

    private String extractedName;

    private String extractedDob;

    private String extractedPan;

    private String extractedAadhaar;

    private String speechText;

    private String status;
}