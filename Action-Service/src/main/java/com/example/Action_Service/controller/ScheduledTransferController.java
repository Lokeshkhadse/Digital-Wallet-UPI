package com.example.Action_Service.controller;

import com.example.Action_Service.dto.GenericResponse;
import com.example.Action_Service.dto.ScheduledTransferRequest;
import com.example.Action_Service.dto.ScheduledTransferResponse;
import com.example.Action_Service.service.ScheduledTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/action/scheduled-transfer")
@RequiredArgsConstructor
public class ScheduledTransferController {

    private final ScheduledTransferService service;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<
            GenericResponse<ScheduledTransferResponse>>
    createSchedule(
            @Valid
            @RequestBody
            ScheduledTransferRequest request){

        ScheduledTransferResponse response =
                service.createSchedule(request);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Scheduled Transfer Created Successfully",
                        response,
                        200
                ));
    }

    @GetMapping("/{scheduleId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<
            GenericResponse<ScheduledTransferResponse>>
    getById(
            @PathVariable Long scheduleId){

        ScheduledTransferResponse response =
                service.getById(scheduleId);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Schedule Found",
                        response,
                        200
                ));
    }

    @PatchMapping("/cancel/{scheduleId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<
            GenericResponse<ScheduledTransferResponse>>
    cancelSchedule(
            @PathVariable Long scheduleId){

        ScheduledTransferResponse response =
                service.cancel(scheduleId);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Scheduled Transfer Cancelled",
                        response,
                        200
                ));
    }
}