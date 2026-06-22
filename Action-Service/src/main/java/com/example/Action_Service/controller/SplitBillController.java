package com.example.Action_Service.controller;

import com.example.Action_Service.dto.*;
import com.example.Action_Service.service.SplitBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/action/split-bill")
@RequiredArgsConstructor
public class SplitBillController {

    private final SplitBillService splitBillService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<
            GenericResponse<SplitBillResponse>>
    create(
            @RequestBody
            CreateSplitBillRequest request){

        SplitBillResponse response =
                splitBillService
                        .createSplitBill(request);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Split Bill Created",
                        response,
                        200));
    }

//    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<
//            GenericResponse<SplitBillResponse>>
//    get(
//            @PathVariable Long id){
//
//        SplitBillResponse response =
//                splitBillService
//                        .getSplitBill(id);
//
//        return ResponseEntity.ok(
//                new GenericResponse<>(
//                        "Split Bill Details",
//                        response,
//                        200));
//    }

    @PostMapping("/pay")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<
            GenericResponse<String>>
    pay(
            @RequestBody
            PaySplitBillRequest request){

        splitBillService.payShare(request);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Share Paid Successfully",
                        "SUCCESS",
                        200));
    }

    @PutMapping("/cancel/{splitBillId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<
            GenericResponse<SplitBillResponse>>
    cancel(
            @PathVariable Long splitBillId,
            @RequestParam Long userId){

        SplitBillResponse response =
                splitBillService.cancel(
                        splitBillId,
                        userId);

        return ResponseEntity.ok(
                new GenericResponse<>(
                        "Split Bill Cancelled",
                        response,
                        200));
    }

//    @GetMapping("/{splitBillId}/summary")
//    public ResponseEntity<
//            GenericResponse<
//                    SettlementSummaryResponse>>
//    summary(
//            @PathVariable Long splitBillId){
//
//        SettlementSummaryResponse response =
//                splitBillService
//                        .getSettlementSummary(
//                                splitBillId);
//
//        return ResponseEntity.ok(
//                new GenericResponse<>(
//                        "Settlement Summary",
//                        response,
//                        200));
//    }
//
//    @GetMapping("/{splitBillId}/participants")
//    public ResponseEntity<
//            GenericResponse<
//                    List<SplitBillParticipantResponse>>>
//    participants(
//            @PathVariable Long splitBillId){
//
//        return ResponseEntity.ok(
//                new GenericResponse<>(
//                        "Participants",
//                        splitBillService
//                                .getParticipants(
//                                        splitBillId),
//                        200));
//    }
//
//    @GetMapping("/created/{userId}")
//    public ResponseEntity<
//            GenericResponse<
//                    List<SplitBillResponse>>>
//    createdBills(
//            @PathVariable Long userId){
//
//        return ResponseEntity.ok(
//                new GenericResponse<>(
//                        "Created Bills",
//                        splitBillService
//                                .getCreatedBills(
//                                        userId),
//                        200));
//    }
//
//    @GetMapping("/pending/{userId}")
//    public ResponseEntity<
//            GenericResponse<
//                    List<SplitBillResponse>>>
//    pendingBills(
//            @PathVariable Long userId){
//
//        return ResponseEntity.ok(
//                new GenericResponse<>(
//                        "Pending Bills",
//                        splitBillService
//                                .getMyPendingBills(
//                                        userId),
//                        200));
//    }
}