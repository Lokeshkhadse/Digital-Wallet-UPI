package com.example.Action_Service.service;

import com.example.Action_Service.dto.*;
import com.example.Action_Service.entity.SplitBill;
import com.example.Action_Service.entity.SplitBillParticipant;
import com.example.Action_Service.enums.SplitBillStatus;
import com.example.Action_Service.enums.SplitPaymentStatus;
import com.example.Action_Service.exception.ValidationException;
import com.example.Action_Service.mapper.SplitBillMapper;
import com.example.Action_Service.mapper.SplitParticipantMapper;
import com.example.Action_Service.repository.SplitBillParticipantRepository;
import com.example.Action_Service.repository.SplitBillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.Action_Service.enums.ActivityType;



import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SplitBillService {

    private final SplitBillRepository splitBillRepository;

    private final SplitBillParticipantRepository participantRepository;

    private final SplitBillMapper mapper;
    private final SplitParticipantMapper participantMapper;

    private final SplitBillActivityService activityService;
    private final TransferService transferService;

    @Transactional
    public SplitBillResponse createSplitBill(
            CreateSplitBillRequest request) {

        validateSplitAmount(request);

        SplitBill splitBill =
                mapper.toEntity(request);

        splitBill.setStatus(
                SplitBillStatus.ACTIVE);

        splitBill.setPaidAmount(
                BigDecimal.ZERO);

        splitBill.setRemainingAmount(
                request.getTotalAmount());

        splitBill.setCreatedAt(
                LocalDateTime.now());

        SplitBill savedBill =
                splitBillRepository.save(splitBill);

        List<SplitBillParticipant> participants =
                new ArrayList<>();

        for (CreateSplitParticipantRequest p :
                request.getParticipants()) {

            SplitBillParticipant participant =
                    SplitBillParticipant.builder()
                            .splitBillId(
                                    savedBill.getSplitBillId())
                            .userId(p.getUserId())
                            .shareAmount(p.getShareAmount())
                            .paidAmount(BigDecimal.ZERO)
                            .remainingAmount(
                                    p.getShareAmount())
                            .paymentStatus(
                                    SplitPaymentStatus.PENDING)
                            .build();

            participants.add(participant);
        }

        participantRepository.saveAll(
                participants);

        activityService.log(
                savedBill,
                ActivityType.CREATED,
                "Split Bill Created",
                request.getCreatedByUserId());

        return mapper.toResponse(savedBill);
    }


    private void validateSplitAmount(
            CreateSplitBillRequest request) {

        BigDecimal total =
                request.getParticipants()
                        .stream()
                        .map(CreateSplitParticipantRequest::getShareAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add);

        if (total.compareTo(
                request.getTotalAmount()) != 0) {

            throw new ValidationException(
                    "Split amount mismatch");
        }
    }

//    public SplitBillResponse getSplitBill(
//            Long splitBillId) {
//
//        SplitBill splitBill =
//                splitBillRepository
//                        .findById(splitBillId)
//                        .orElseThrow(() ->
//                                new ValidationException(
//                                        "Split Bill not found"));
//
//        return mapper.toResponse(splitBill);
//    }


    @Transactional
    public SplitBillResponse cancel(
            Long splitBillId,
            Long userId) {

        SplitBill splitBill =
                splitBillRepository
                        .findById(splitBillId)
                        .orElseThrow(() ->
                                new ValidationException(
                                        "Split Bill not found"));

        splitBill.setStatus(
                SplitBillStatus.CANCELLED);

        splitBillRepository.save(
                splitBill);

        activityService.log(
                splitBill,
                ActivityType.CANCELLED,
                "Split Bill Cancelled",
                userId);

        return mapper.toResponse(
                splitBill);
    }


    @Transactional
    public void markParticipantPaid(
            Long participantId,
            String transactionRefNo,
            Long participantBankId ) {

        SplitBillParticipant participant =
                participantRepository
                        .findById(participantId)
                        .orElseThrow(() ->
                                new ValidationException(
                                        "Participant not found"));

        participant.setPaymentStatus(
                SplitPaymentStatus.PAID);

        participant.setTransactionRefNo(
                transactionRefNo);

        participant.setUserBankId(participantBankId);

        participant.setPaidAmount(
                participant.getShareAmount());

        participant.setRemainingAmount(
                BigDecimal.ZERO);

        participant.setPaidAt(
                LocalDateTime.now());

        participantRepository.save(
                participant);

        SplitBill splitBill =
                splitBillRepository
                        .findById(
                                participant.getSplitBillId())
                        .orElseThrow(() ->
                                new ValidationException(
                                        "Split Bill not found"));

        splitBill.setPaidAmount(
                splitBill.getPaidAmount()
                        .add(
                                participant.getShareAmount()));

        splitBill.setRemainingAmount(
                splitBill.getRemainingAmount()
                        .subtract(
                                participant.getShareAmount()));

        splitBillRepository.save(
                splitBill);

        updateBillStatus(
                splitBill.getSplitBillId());
    }

    private void updateBillStatus(
            Long splitBillId) {

        SplitBill splitBill =
                splitBillRepository.findById(
                                splitBillId)
                        .orElseThrow(() ->
                                new ValidationException(
                                        "Split Bill not found"));

        long totalParticipants =
                participantRepository.countBySplitBillId(
                        splitBillId);

        long paidParticipants =
                participantRepository
                        .countBySplitBillIdAndPaymentStatus(
                                splitBillId,
                                SplitPaymentStatus.PAID);

        if (paidParticipants == 0) {

            splitBill.setStatus(
                    SplitBillStatus.ACTIVE);

        } else if (paidParticipants < totalParticipants) {

            splitBill.setStatus(
                    SplitBillStatus.PARTIALLY_PAID);
            splitBill.setUpdatedAt(LocalDateTime.now());


        } else {

            splitBill.setStatus(
                    SplitBillStatus.FULLY_PAID);
            splitBill.setUpdatedAt(LocalDateTime.now());

            activityService.log(
                    splitBill,
                    ActivityType.COMPLETED,
                    "Split Bill Fully Settled",
                    splitBill.getCreatedByUserId());
        }

        splitBillRepository.save(
                splitBill);
    }


    @Transactional
    public void payShare(
            PaySplitBillRequest request) {

        SplitBillParticipant participant =
                participantRepository
                        .findBySplitBillIdAndUserId(
                                request.getSplitBillId(),
                                request.getParticipantUserId())
                        .orElseThrow(() ->
                                new ValidationException(
                                        "Participant not found"));

        if(participant.getPaymentStatus()
                == SplitPaymentStatus.PAID){

            throw new ValidationException(
                    "Share already paid");
        }

        SplitBill splitBill =
                splitBillRepository
                        .findById(
                                request.getSplitBillId())
                        .orElseThrow(() ->
                                new ValidationException(
                                        "Split Bill not found"));

        TransferTransactionRequest transferRequest =
                TransferTransactionRequest.builder()
                        .senderUserId(
                                participant.getUserId())

                        .senderBankId(
                                request.getParticipantBankId())

                        .receiverUserId(
                                splitBill.getCreatedByUserId())

                        .receiverBankId(
                                splitBill.getReceiverBankId())

                        .amount(
                                participant.getShareAmount())

                        .upiPin(
                                request.getUpiPin())

                        .transactionType(
                                "TRANSFER")

                        .remarks(
                                "Split Bill Settlement")
                        .build();

        TransferTransactionResponse response =
                transferService.transfer(
                        transferRequest);

        markParticipantPaid(
                participant.getParticipantId(),
                response.getTransactionRefNo(),
                request.getParticipantBankId());

        activityService.log(
                splitBill,
                ActivityType.PAYMENT_RECEIVED,
                "Participant paid share",
                participant.getUserId());
    }

//    public SettlementSummaryResponse getSettlementSummary(
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
//    public List<SplitBillParticipantResponse>
//    getParticipants(
//            Long splitBillId) {
//
//        return participantRepository
//                .findBySplitBillId(
//                        splitBillId)
//                .stream()
//                .map(participantMapper::toResponse)
//                .toList();
//    }
//
//    public List<SplitBillResponse>
//    getCreatedBills(
//            Long userId) {
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
//    getMyPendingBills(
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
//            SplitBill bill =
//                    splitBillRepository
//                            .findById(
//                                    participant.getSplitBillId())
//                            .orElse(null);
//
//            if (bill != null) {
//
//                response.add(
//                        mapper.toResponse(
//                                bill));
//            }
//        }
//
//        return response;
//    }

}