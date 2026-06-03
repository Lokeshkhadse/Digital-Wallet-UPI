package com.example.Action_Service.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UserResponseDto {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String roles;
}
