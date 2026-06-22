//package com.example.Query_Service.service;
//
//import com.example.Query_Service.dto.SettlementSummaryResponse;
//import com.example.Query_Service.dto.SplitBillActivityResponse;
//import com.example.Query_Service.dto.SplitBillParticipantResponse;
//import com.example.Query_Service.dto.SplitBillResponse;
//import com.example.Query_Service.entity.SplitBill;
//import com.example.Query_Service.entity.SplitBillParticipant;
//import com.example.Query_Service.enums.SplitPaymentStatus;
//import com.example.Query_Service.exception.ValidationException;
//import com.example.Query_Service.mapper.SplitBillMapper;
//import com.example.Query_Service.mapper.SplitParticipantMapper;
//import com.example.Query_Service.repository.SplitBillActivityLogRepository;
//import com.example.Query_Service.repository.SplitBillParticipantRepository;
//import com.example.Query_Service.repository.SplitBillRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class SplitBillService {
//
//    private final SplitBillRepository splitBillRepository;
//
//    private final SplitBillParticipantRepository participantRepository;
//
//    private final SplitBillActivityLogRepository activityRepository;
//
//   private final SplitBillMapper mapper;
//    private final SplitParticipantMapper participantMapper;
//
//
//    public SplitBillResponse getSplitBill(
//            Long splitBillId){
//
//        SplitBill splitBill =
//                splitBillRepository
//                        .findById(splitBillId)
//                        .orElseThrow(() ->
//                                new ValidationException(
//                                        "Split Bill not found"));
//
//        List<SplitBillParticipantResponse> participants =
//                getParticipants(splitBillId);
//
//        return mapper.toResponse(
//                splitBill,participants);
//    }
//
//    public List<SplitBillParticipantResponse>
//    getParticipants(
//            Long splitBillId){
//
//        return participantRepository
//                .findBySplitBillId(
//                        splitBillId)
//                .stream()
//                .map(participantMapper::toResponse)
//                .toList();
//    }
//
//    public SettlementSummaryResponse
//    getSettlementSummary(
//            Long splitBillId){
//
//        SplitBill splitBill =
//                splitBillRepository
//                        .findById(splitBillId)
//                        .orElseThrow(() ->
//                                new ValidationException(
//                                        "Split Bill not found"));
//
//        long totalParticipants =
//                participantRepository
//                        .countBySplitBillId(
//                                splitBillId);
//
//        long paidParticipants =
//                participantRepository
//                        .countBySplitBillIdAndPaymentStatus(
//                                splitBillId,
//                                SplitPaymentStatus.PAID);
//
//        return SettlementSummaryResponse
//                .builder()
//                .splitBillId(
//                        splitBillId)
//                .totalAmount(
//                        splitBill.getTotalAmount())
//                .paidAmount(
//                        splitBill.getPaidAmount())
//                .remainingAmount(
//                        splitBill.getRemainingAmount())
//                .totalParticipants(
//                        (int) totalParticipants)
//                .paidParticipants(
//                        (int) paidParticipants)
//                .pendingParticipants(
//                        (int) (totalParticipants
//                                - paidParticipants))
//                .build();
//    }
//
//
//    public List<SplitBillResponse>
//    getCreatedBills(
//            Long userId){
//
//        return splitBillRepository
//                .findByCreatedByUserIdOrderByCreatedAtDesc(
//                        userId)
//                .stream()
//                .map(mapper::toResponse)
//                .toList();
//    }
//
//    public List<SplitBillResponse>
//    getPendingBills(
//            Long userId){
//
//        List<SplitBillParticipant>
//                participants =
//                participantRepository
//                        .findByUserIdAndPaymentStatus(
//                                userId,
//                                SplitPaymentStatus.PENDING);
//
//        List<SplitBillResponse>
//                response =
//                new ArrayList<>();
//
//        for (SplitBillParticipant participant :
//                participants){
//
//            splitBillRepository
//                    .findById(
//                            participant.getSplitBillId())
//                    .ifPresent(bill ->
//                            response.add(
//                                    mapper.toResponse(
//                                            bill)));
//        }
//
//        return response;
//    }
//
//    public List<SplitBillActivityResponse>
//    getActivityLogs(
//            Long splitBillId){
//
//        return activityRepository
//                .findBySplitBillIdOrderByCreatedAtDesc(
//                        splitBillId)
//                .stream()
//                .map(log ->
//                        SplitBillActivityResponse
//                                .builder()
//                                .activityId(
//                                        log.getActivityId())
//                                .userId(
//                                        log.getUserId())
//                                .activityType(
//                                        log.getActivityType().name())
//                                .remarks(
//                                        log.getRemarks())
//                                .createdAt(
//                                        log.getCreatedAt())
//                                .build())
//                .toList();
//    }
//
//}


package com.example.Query_Service.service;

import com.example.Query_Service.dto.*;
import com.example.Query_Service.entity.SplitBill;
import com.example.Query_Service.entity.SplitBillParticipant;
import com.example.Query_Service.enums.SplitPaymentStatus;
import com.example.Query_Service.exception.ValidationException;
import com.example.Query_Service.mapper.SplitBillMapper;
import com.example.Query_Service.mapper.SplitParticipantMapper;
import com.example.Query_Service.repository.SplitBillActivityLogRepository;
import com.example.Query_Service.repository.SplitBillParticipantRepository;
import com.example.Query_Service.repository.SplitBillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SplitBillService {

    private final SplitBillRepository splitBillRepository;

    private final SplitBillParticipantRepository participantRepository;

    private final SplitBillActivityLogRepository activityRepository;

    private final SplitBillMapper mapper;

    private final SplitParticipantMapper participantMapper;


    public SplitBillResponse getSplitBill(
            Long splitBillId) {

        SplitBill splitBill =
                splitBillRepository
                        .findById(splitBillId)
                        .orElseThrow(() ->
                                new ValidationException(
                                        "Split Bill not found"));

        List<SplitBillParticipantResponse> participants =
                getParticipants(splitBillId);

        return mapper.toResponse(
                splitBill,
                participants);
    }


    public List<SplitBillParticipantResponse>
    getParticipants(
            Long splitBillId) {

        return participantRepository
                .findBySplitBillId(splitBillId)
                .stream()
                .map(participantMapper::toResponse)
                .toList();
    }


    public SettlementSummaryResponse
    getSettlementSummary(
            Long splitBillId) {

        SplitBill splitBill =
                splitBillRepository
                        .findById(splitBillId)
                        .orElseThrow(() ->
                                new ValidationException(
                                        "Split Bill not found"));

        long totalParticipants =
                participantRepository
                        .countBySplitBillId(
                                splitBillId);

        long paidParticipants =
                participantRepository
                        .countBySplitBillIdAndPaymentStatus(
                                splitBillId,
                                SplitPaymentStatus.PAID);

        return SettlementSummaryResponse
                .builder()
                .splitBillId(splitBillId)
                .totalAmount(splitBill.getTotalAmount())
                .paidAmount(splitBill.getPaidAmount())
                .remainingAmount(splitBill.getRemainingAmount())
                .totalParticipants((int) totalParticipants)
                .paidParticipants((int) paidParticipants)
                .pendingParticipants(
                        (int) (totalParticipants - paidParticipants))
                .build();
    }


    public List<SplitBillResponse>
    getCreatedBills(
            Long userId) {

        return splitBillRepository
                .findByCreatedByUserIdOrderByCreatedAtDesc(
                        userId)
                .stream()
                .map(bill -> {

                    List<SplitBillParticipantResponse>
                            participants =
                            getParticipants(
                                    bill.getSplitBillId());

                    return mapper.toResponse(
                            bill,
                            participants);
                })
                .toList();
    }


    public List<SplitBillResponse>
    getPendingBills(
            Long userId) {

        List<SplitBillParticipant> participants =
                participantRepository
                        .findByUserIdAndPaymentStatus(
                                userId,
                                SplitPaymentStatus.PENDING);

        List<SplitBillResponse> response =
                new ArrayList<>();

        for (SplitBillParticipant participant :
                participants) {

            splitBillRepository
                    .findById(
                            participant.getSplitBillId())
                    .ifPresent(bill -> {

                        List<SplitBillParticipantResponse>
                                participantResponses =
                                getParticipants(
                                        bill.getSplitBillId());

                        response.add(
                                mapper.toResponse(
                                        bill,
                                        participantResponses));
                    });
        }

        return response;
    }


    public List<SplitBillActivityResponse>
    getActivityLogs(
            Long splitBillId) {

        return activityRepository
                .findBySplitBillIdOrderByCreatedAtDesc(
                        splitBillId)
                .stream()
                .map(log ->
                        SplitBillActivityResponse
                                .builder()
                                .activityId(
                                        log.getActivityId())
                                .userId(
                                        log.getUserId())
                                .activityType(
                                        log.getActivityType().name())
                                .remarks(
                                        log.getRemarks())
                                .createdAt(
                                        log.getCreatedAt())
                                .build())
                .toList();
    }
}