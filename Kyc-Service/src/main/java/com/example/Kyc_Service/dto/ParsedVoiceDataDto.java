package com.example.Kyc_Service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedVoiceDataDto {

    private String name;

    private String dob;

    private String pan;

    private String aadhaar;
}