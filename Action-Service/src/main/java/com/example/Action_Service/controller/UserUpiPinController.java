package com.example.Action_Service.controller;

import com.example.Action_Service.dto.ChangePinRequest;
import com.example.Action_Service.dto.CreatePinRequest;
import com.example.Action_Service.service.UserUpiPinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/action/upi-pin")
@RequiredArgsConstructor
public class UserUpiPinController {

    private final UserUpiPinService service;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> createPin(
            @Valid @RequestBody CreatePinRequest request) {

        return ResponseEntity.ok(
                service.createPin(request));
    }

    @PostMapping("/change")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> changePin(
            @Valid @RequestBody ChangePinRequest request) {

        return ResponseEntity.ok(service.changePin(request));
    }
}

//123456 user1
//234567 user2
//345678 user3
//456789  user4