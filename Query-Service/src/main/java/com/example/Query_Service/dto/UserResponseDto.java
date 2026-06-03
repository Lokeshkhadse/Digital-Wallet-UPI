package com.example.Query_Service.dto;

import lombok.Data;

@Data
public class UserResponseDto {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String roles;
}
