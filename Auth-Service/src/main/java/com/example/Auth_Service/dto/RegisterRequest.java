package com.example.Auth_Service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {

    @NotBlank
    private String name;

    @Email
    private String email;

    @NotBlank
    private String password;

    private Set<String> roles;

    @NotBlank(message = "Date of Birth is required")
//    @Pattern(
//            regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
//            message = "Date of Birth must be in YYYY-MM-DD format"
//    )
    @Pattern(
            regexp = "^(0[1-9]|[12]\\d|3[01])-(0[1-9]|1[0-2])-\\d{4}$",
            message = "Date of Birth must be in DD-MM-YYYY format"
    )
    private String dob;
}
