package com.example.Action_Service.feign;

import com.example.Action_Service.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "auth-service", url = "http://localhost:8081")
@FeignClient(name = "AUTH-SERVICE")
public interface AuthServiceClient {


    @GetMapping("/auth/users/{id}")
    UserResponseDto getUserById(@PathVariable Long id);
}
