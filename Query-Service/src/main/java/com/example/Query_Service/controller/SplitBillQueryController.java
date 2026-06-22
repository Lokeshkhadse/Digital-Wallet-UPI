package com.example.Query_Service.controller;

import com.example.Query_Service.dto.GenericResponse;
import com.example.Query_Service.dto.SettlementSummaryResponse;
import com.example.Query_Service.dto.SplitBillParticipantResponse;
import com.example.Query_Service.dto.SplitBillResponse;
import com.example.Query_Service.service.SplitBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/query/split-bill")
@RequiredArgsConstructor
public class SplitBillQueryController {

    private final SplitBillService splitBillService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<
            GenericResponse<SplitBillResponse>>
    get(
            @PathVariable Long id){

        SplitBillResponse response =
                splitBillService
                        .getSplitBill(id);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Split Bill Details",
                        response,
                        200));
    }


    @GetMapping("/{splitBillId}/summary")
    public ResponseEntity<
            GenericResponse<
                    SettlementSummaryResponse>>
    summary(
            @PathVariable Long splitBillId){

        SettlementSummaryResponse response =
                splitBillService
                        .getSettlementSummary(
                                splitBillId);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Settlement Summary",
                        response,
                        200));
    }

    @GetMapping("/{splitBillId}/participants")
    public ResponseEntity<
            GenericResponse<
                    List<SplitBillParticipantResponse>>>
    participants(
            @PathVariable Long splitBillId){

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Participants",
                        splitBillService
                                .getParticipants(
                                        splitBillId),
                        200));
    }

    @GetMapping("/created/{userId}")
    public ResponseEntity<
            GenericResponse<
                    List<SplitBillResponse>>>
    createdBills(
            @PathVariable Long userId){

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Created Bills",
                        splitBillService
                                .getCreatedBills(
                                        userId),
                        200));
    }

    @GetMapping("/pending/{userId}")
    public ResponseEntity<
            GenericResponse<
                    List<SplitBillResponse>>>
    pendingBills(
            @PathVariable Long userId){

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Pending Bills",
                        splitBillService
                                .getPendingBills(
                                        userId),
                        200));
    }


}
