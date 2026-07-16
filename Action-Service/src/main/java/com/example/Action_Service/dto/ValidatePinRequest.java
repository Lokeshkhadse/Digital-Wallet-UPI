package com.example.Action_Service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ValidatePinRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "User Bank ID is required")
    private Long userBankId;

    @NotBlank(message = "UPI PIN is required")
    private String upiPin;
}