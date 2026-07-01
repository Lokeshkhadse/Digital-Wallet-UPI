package com.example.Action_Service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreatePinRequest {

    private Long userId;

    private Long userBankId;

    @NotBlank(message = "PIN is required")
    @Pattern(regexp = "\\d{6}", message = "PIN must be exactly 6 digits")
    private String pin;
}