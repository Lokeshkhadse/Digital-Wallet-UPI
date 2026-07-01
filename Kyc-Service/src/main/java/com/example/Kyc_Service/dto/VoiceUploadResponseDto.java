package com.example.Kyc_Service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VoiceUploadResponseDto {

    private Long voiceVerificationId;

    private Long userId;

    private String message;
}