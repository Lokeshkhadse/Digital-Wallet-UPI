package com.example.Action_Service.feign;

import com.example.Action_Service.dto.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public class AuthServiceFallback implements AuthServiceClient {

    @Override
    public UserResponseDto getUserById(Long id) {

        UserResponseDto dto = new UserResponseDto();
        dto.setId(id);
        dto.setName("AUTH SERVICE DOWN - FALLBACK EXECUTED");
        return dto;
    }
}