package com.example.Action_Service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ChangePinRequest {

    private Long userId;

    private Long userBankId;

    @NotBlank(message = "Old PIN is required")
    @Pattern(regexp = "\\d{6}", message = "Old PIN must be exactly 6 digits")
    private String oldPin;

    @NotBlank(message = "New PIN is required")
    @Pattern(regexp = "\\d{6}", message = "New PIN must be exactly 6 digits")
    private String newPin;
}
