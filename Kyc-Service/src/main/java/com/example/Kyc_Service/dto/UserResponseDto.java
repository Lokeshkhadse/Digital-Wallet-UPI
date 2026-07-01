package com.example.Kyc_Service.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String roles;
    private String dob;
}
