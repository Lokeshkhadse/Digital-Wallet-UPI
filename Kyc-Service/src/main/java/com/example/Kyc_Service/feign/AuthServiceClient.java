package com.example.Kyc_Service.feign;

import com.example.Kyc_Service.dto.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "auth-service", url = "http://localhost:8081")
//@FeignClient(
//        name = "AUTH-SERVICE"
//       // , fallback = AuthServiceFallback.class
//)
//@FeignClient(name = "AUTH-SERVICE", url = "http://localhost:8081")
//public interface AuthServiceClient {
//
//    @GetMapping("/auth/users/{id}")
//    UserResponseDto getUserById(@PathVariable Long id);
//}

@FeignClient(
        name = "AUTH-SERVICE",
        url = "${services.auth.url}"
)
public interface AuthServiceClient {

    @GetMapping("/auth/users/{id}")
    UserResponseDto getUserById(@PathVariable Long id);
}
