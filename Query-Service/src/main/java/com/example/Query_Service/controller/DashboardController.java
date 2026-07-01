package com.example.Query_Service.controller;

import com.example.Query_Service.dto.*;
import com.example.Query_Service.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/query/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GenericResponse<DashboardHomeResponse>>
    userDashboard(
            @PathVariable Long userId) {

        DashboardHomeResponse response =
                dashboardService.getUserDashboard(userId);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Dashboard fetched successfully",
                        response,
                        200
                )
        );
    }

    @GetMapping("/account/{bankId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<GenericResponse<AccountDashboardResponse>>
    accountDashboard(
            @PathVariable Long bankId) {

        AccountDashboardResponse response =
                dashboardService.getAccountDashboard(bankId);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Account dashboard fetched successfully",
                        response,
                        200
                )
        );
    }
}