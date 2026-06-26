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
import com.example.Query_Service.exception.ResourceNotFoundException;
import com.example.Query_Service.exception.ValidationException;
import com.example.Query_Service.mapper.SplitBillMapper;
import com.example.Query_Service.mapper.SplitParticipantMapper;
import com.example.Query_Service.repository.SplitBillActivityLogRepository;
import com.example.Query_Service.repository.SplitBillParticipantRepository;
import com.example.Query_Service.repository.SplitBillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SplitBillService {

    private final SplitBillRepository splitBillRepository;

    private final SplitBillParticipantRepository participantRepository;

    private final SplitBillActivityLogRepository activityRepository;

    private final SplitBillMapper splitBillmapper;

    private final SplitParticipantMapper splitParticipantMapper;

    @Qualifier("queryExecutor")
    private final Executor queryExecutor;


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

        return splitBillmapper.toResponse(
                splitBill,
                participants);
    }


    public List<SplitBillParticipantResponse>
    getParticipants(
            Long splitBillId) {

        return participantRepository
                .findBySplitBillId(splitBillId)
                .stream()
                .map(splitParticipantMapper::toResponse)
                .toList();
    }


//    public SettlementSummaryResponse
//    getSettlementSummary(
//            Long splitBillId) {
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
//                .splitBillId(splitBillId)
//                .totalAmount(splitBill.getTotalAmount())
//                .paidAmount(splitBill.getPaidAmount())
//                .remainingAmount(splitBill.getRemainingAmount())
//                .totalParticipants((int) totalParticipants)
//                .paidParticipants((int) paidParticipants)
//                .pendingParticipants(
//                        (int) (totalParticipants - paidParticipants))
//                .build();
//    }

    public SettlementSummaryResponse
    getSettlementSummary(
            Long splitBillId) {


        CompletableFuture<SplitBill> splitBillFuture =
                 CompletableFuture.supplyAsync(() ->
                           splitBillRepository.findById(splitBillId)
                                   .orElseThrow(() -> new ValidationException("Split Bill not found"))
                         ,queryExecutor)
                         .exceptionally(ex -> {
                             throw new ValidationException(
                                     "Split Bill not found");
                         });

        CompletableFuture<Long> totalParticipantsFuture =
                 CompletableFuture.supplyAsync(() ->
                         participantRepository.countBySplitBillId(splitBillId)
                         ,queryExecutor)
                         .exceptionally(ex -> {
                             throw new ValidationException(
                                     "Unable to fetch total participants.");
                         });

        CompletableFuture<Long> paidParticipantsFuture =
                CompletableFuture
                        .supplyAsync(() ->
                                        participantRepository
                                                .countBySplitBillIdAndPaymentStatus(
                                                        splitBillId,
                                                        SplitPaymentStatus.PAID),
                                queryExecutor)
                        .exceptionally(ex -> {
                            throw new ValidationException(
                                    "Unable to fetch paid participants.");
                        });

        CompletableFuture.allOf(splitBillFuture,totalParticipantsFuture,paidParticipantsFuture).join();

        SplitBill splitBill = splitBillFuture.join();
        long totalParticipants = totalParticipantsFuture.join();
        long paidParticipants = paidParticipantsFuture.join();

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

 // k+1 problem code
//    public List<SplitBillResponse>
//    getCreatedBills(
//            Long userId) {
//
//        return splitBillRepository
//                .findByCreatedByUserIdOrderByCreatedAtDesc(
//                        userId)
//                .stream()
//                .map(bill -> {
//
//                    List<SplitBillParticipantResponse>
//                            participants =
//                            getParticipants(
//                                    bill.getSplitBillId());
//
//                    return splitBillmapper.toResponse(
//                            bill,
//                            participants);
//                })
//                .toList();
//    }
//

    public List<SplitBillResponse> getCreatedBills(
            Long userId) {

        List<SplitBill> bills = splitBillRepository.findByCreatedByUserIdOrderByCreatedAtDesc(userId);

        if(bills.isEmpty()){
            return Collections.emptyList();
        }
        List<Long> billsId = bills.stream().map(SplitBill::getSplitBillId).toList();

        List<SplitBillParticipant> allParticipants = participantRepository.findBySplitBillIdIn(billsId);

        Map<Long,List<SplitBillParticipantResponse>> participantMap =
                 allParticipants.stream()
                         .collect(Collectors.groupingBy(
                                 SplitBillParticipant::getSplitBillId,
                                 Collectors.mapping(
                                         splitParticipantMapper::toResponse,
                                         Collectors.toList()
                                 )
                         ));

        return bills.stream()
                .map(bill -> {
                    List<SplitBillParticipantResponse>  participants =
                            participantMap.getOrDefault(
                                    bill.getSplitBillId(),
                                    Collections.emptyList()
                            );
                    return splitBillmapper.toResponse(
                            bill,
                            participants);

                })
                .toList();

    }



    //k+1 problem code
//    public List<SplitBillResponse> getPendingBills(
//            Long userId) {
//
//        List<SplitBillParticipant> participants =
//                participantRepository
//                        .findByUserIdAndPaymentStatus(
//                                userId,
//                                SplitPaymentStatus.PENDING);
//
//        List<SplitBillResponse> response =
//                new ArrayList<>();
//
//        for (SplitBillParticipant participant :
//                participants) {
//
//            splitBillRepository
//                    .findById(
//                            participant.getSplitBillId())
//                    .ifPresent(bill -> {
//
//                        List<SplitBillParticipantResponse>
//                                participantResponses =
//                                getParticipants(
//                                        bill.getSplitBillId());
//
//                        response.add(
//                                mapper.toResponse(
//                                        bill,
//                                        participantResponses));
//                    });
//        }
//
//        return response;
//    }


    public List<SplitBillResponse> getPendingBills(Long userId) {

        List<SplitBillParticipant> pendingParticipants =
                participantRepository.findByUserIdAndPaymentStatus(
                        userId,
                        SplitPaymentStatus.PENDING);

        if (pendingParticipants.isEmpty()) {
            return Collections.emptyList();
        }

        // Get all unique Split Bill IDs
        List<Long> splitBillIds =
                pendingParticipants.stream()
                        .map(SplitBillParticipant::getSplitBillId)
                        .distinct()
                        .toList();

        // Fetch all bills in a single query
        List<SplitBill> bills =
                splitBillRepository.findBySplitBillIdIn(splitBillIds);

        // Fetch all participants of all bills in a single query
        List<SplitBillParticipant> allParticipants =
                participantRepository.findBySplitBillIdIn(splitBillIds);

        // Group participants by Split Bill ID
        Map<Long, List<SplitBillParticipantResponse>> participantMap =
                allParticipants.stream()
                        .collect(Collectors.groupingBy(
                                SplitBillParticipant::getSplitBillId,
                                Collectors.mapping(
                                        splitParticipantMapper::toResponse,
                                        Collectors.toList()
                                )));

        // Build final response
        return bills.stream()
                .map(bill -> {

                    List<SplitBillParticipantResponse> participants =
                            participantMap.getOrDefault(
                                    bill.getSplitBillId(),
                                    Collections.emptyList());

                    return splitBillmapper.toResponse(
                            bill,
                            participants);

                })
                .toList();
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