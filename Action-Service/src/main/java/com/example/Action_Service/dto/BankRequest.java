package com.example.Action_Service.dto;

import com.example.Action_Service.enums.AccountType;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


    @Data
    public class BankRequest {

        @NotNull(message = "User ID is required")
        @Positive(message = "User ID must be a positive number")
        private Long userId;

        @NotBlank(message = "Bank name is required")
        @Size(min = 3, max = 100, message = "Bank name must be between 3 and 100 characters")
        private String bankName;

        @NotBlank(message = "Account number is required")
        @Pattern(
                regexp = "^[0-9]{9,18}$",
                message = "Account number must be between 9 and 18 digits"
        )
        private String accountNumber;

        @NotBlank(message = "IFSC code is required")
        @Pattern(
                regexp = "^[A-Z]{4}0[A-Z0-9]{6}$",
                message = "Invalid IFSC code (Example: SBIN0001234)"
        )
        private String ifscCode;

        @NotBlank(message = "UPI ID is required")
        @Pattern(
                regexp = "^[a-zA-Z0-9._-]{2,256}@[a-zA-Z]{2,64}$",
                message = "Invalid UPI ID (Example: lokesh@okhdfcbank)"
        )
        private String upiId;

        @NotNull(message = "Account type is required")
        private AccountType accountType;
    }

