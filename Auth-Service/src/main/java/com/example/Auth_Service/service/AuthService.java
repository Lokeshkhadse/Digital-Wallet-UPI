package com.example.Auth_Service.service;

import com.example.Auth_Service.dto.AuthResponse;
import com.example.Auth_Service.dto.LoginRequest;
import com.example.Auth_Service.dto.RegisterRequest;
import com.example.Auth_Service.entity.User;
import com.example.Auth_Service.exception.EmailAlreadyExistException;
import com.example.Auth_Service.exception.UserNotFoundException;
import com.example.Auth_Service.repository.UserRepository;
import com.example.Auth_Service.security.CustomUserDetails;
import com.example.Auth_Service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public Map<String, Object> register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistException("Email Already Exists Use another email");
        }

        Set<String> requestRoles = request.getRoles();

        if (requestRoles == null || requestRoles.isEmpty()) {
            requestRoles = new HashSet<>();
            requestRoles.add("USER");
        }

        String roles = String.join(",", requestRoles);

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User Registered Successfully");
        response.put("user", user);

        return response;
    }


    public AuthResponse login(LoginRequest request) {

        // 1. Check email first
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("Invalid email: user not found"));



        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );


        UserDetails userDetails = new CustomUserDetails(user);


        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .email(user.getEmail())
                .build();
    }

    public Map<String, Object> getAllUsers() {
        Map<String, Object> map = new HashMap<>();
        List<User> users = userRepository.findAll();
       return Map.of("users", users,
                  "message","successfully fetched all users",
                  "status",200
                   );
    }

    public AuthResponse refreshToken(String refreshToken) {

        String email = jwtUtil.extractUsername(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));

        // ✅ Use CustomUserDetails consistently (was using manual builder before)
        UserDetails userDetails = new CustomUserDetails(user);

        String newAccessToken = jwtUtil.generateToken(userDetails);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .email(user.getEmail())
                .build();
    }

    public Map<String, Object> updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User Not Found"));

        user.setPassword(passwordEncoder.encode(newPassword));

       User updatedUser = userRepository.save(user);

        return Map.of("status", HttpStatus.ACCEPTED,
                  "message", "Password updated successfully",
                   "user", updatedUser);
    }

    public Map<String, Object> getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User Not Found"));

        return Map.of("status", HttpStatus.OK,
                  "message", "User fetched successfully",
                   "user", user);
    }
}