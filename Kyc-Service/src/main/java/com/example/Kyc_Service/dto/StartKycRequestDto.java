package com.example.Kyc_Service.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartKycRequestDto {

    @NotNull
    private Long userId;
}