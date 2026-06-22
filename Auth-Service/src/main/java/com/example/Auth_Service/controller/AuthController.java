package com.example.Auth_Service.controller;


import com.example.Auth_Service.dto.AuthResponse;
import com.example.Auth_Service.dto.LoginRequest;
import com.example.Auth_Service.dto.RegisterRequest;
import com.example.Auth_Service.dto.UserResponseDto;
import com.example.Auth_Service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>>
    register(
            @Valid
            @RequestBody RegisterRequest request) {

        Map<String,Object> response = authService.register(request);

        return ResponseEntity.status(
                        HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse>
    login(
            @Valid
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request));
    }

    @PatchMapping("/update-password/{email}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Map<String, Object>> updatePassword(@PathVariable String email, @RequestParam String newPassword) {
        Map<String, Object> response = authService.updatePassword(email, newPassword);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>>
    getAllUsers() {

        return ResponseEntity.ok(
                authService.getAllUsers());
    }

    @PostMapping("/refresh")
   // @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<AuthResponse>
    refreshToken(
            @RequestParam String refreshToken) {

        return ResponseEntity.ok(
                authService.refreshToken(refreshToken));
    }

//    @GetMapping("/users/{id}")
//   // @PreAuthorize("hasAnyRole('USER','ADMIN')")
//    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
//        Map<String, Object> response = authService.getUserById(id);
//        return ResponseEntity.ok(response);
//    }


    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                authService.getUserById(id)
        );
    }
}


