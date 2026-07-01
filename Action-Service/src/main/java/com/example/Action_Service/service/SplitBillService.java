//package com.example.Action_Service.service;
//
//import com.example.Action_Service.dto.*;
//import com.example.Action_Service.entity.SplitBill;
//import com.example.Action_Service.entity.SplitBillParticipant;
//import com.example.Action_Service.enums.SplitBillStatus;
//import com.example.Action_Service.enums.SplitPaymentStatus;
//import com.example.Action_Service.exception.ValidationException;
//import com.example.Action_Service.mapper.SplitBillMapper;
//import com.example.Action_Service.mapper.SplitParticipantMapper;
//import com.example.Action_Service.repository.SplitBillParticipantRepository;
//import com.example.Action_Service.repository.SplitBillRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import com.example.Action_Service.enums.ActivityType;
//
//
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
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
//    private final SplitBillMapper mapper;
//    private final SplitParticipantMapper participantMapper;
//
//    private final SplitBillActivityService activityService;
//    private final TransferService transferService;
//
//    @Transactional
//    public SplitBillResponse createSplitBill(
//            CreateSplitBillRequest request) {
//
//        validateSplitAmount(request);
//
//        SplitBill splitBill =
//                mapper.toEntity(request);
//
//        splitBill.setStatus(
//                SplitBillStatus.ACTIVE);
//
//        splitBill.setPaidAmount(
//                BigDecimal.ZERO);
//
//        splitBill.setRemainingAmount(
//                request.getTotalAmount());
//
//        splitBill.setCreatedAt(
//                LocalDateTime.now());
//
//        SplitBill savedBill =
//                splitBillRepository.save(splitBill);
//
//        List<SplitBillParticipant> participants =
//                new ArrayList<>();
//
//        for (CreateSplitParticipantRequest p :
//                request.getParticipants()) {
//
//            SplitBillParticipant participant =
//                    SplitBillParticipant.builder()
//                            .splitBillId(
//                                    savedBill.getSplitBillId())
//                            .userId(p.getUserId())
//                            .shareAmount(p.getShareAmount())
//                            .paidAmount(BigDecimal.ZERO)
//                            .remainingAmount(
//                                    p.getShareAmount())
//                            .paymentStatus(
//                                    SplitPaymentStatus.PENDING)
//                            .build();
//
//            participants.add(participant);
//        }
//
//        participantRepository.saveAll(
//                participants);
//
//        activityService.log(
//                savedBill,
//                ActivityType.CREATED,
//                "Split Bill Created",
//                request.getCreatedByUserId());
//
//        return mapper.toResponse(savedBill);
//    }
//
//
//    private void validateSplitAmount(
//            CreateSplitBillRequest request) {
//
//        BigDecimal total =
//                request.getParticipants()
//                        .stream()
//                        .map(CreateSplitParticipantRequest::getShareAmount)
//                        .reduce(
//                                BigDecimal.ZERO,
//                                BigDecimal::add);
//
//        if (total.compareTo(
//                request.getTotalAmount()) != 0) {
//
//            throw new ValidationException(
//                    "Split amount mismatch");
//        }
//    }
//
////    public SplitBillResponse getSplitBill(
////            Long splitBillId) {
////
////        SplitBill splitBill =
////                splitBillRepository
////                        .findById(splitBillId)
////                        .orElseThrow(() ->
////                                new ValidationException(
////                                        "Split Bill not found"));
////
////        return mapper.toResponse(splitBill);
////    }
//
//
//    @Transactional
//    public SplitBillResponse cancel(
//            Long splitBillId,
//            Long userId) {
//
//        SplitBill splitBill =
//                splitBillRepository
//                        .findById(splitBillId)
//                        .orElseThrow(() ->
//                                new ValidationException(
//                                        "Split Bill not found"));
//
//        splitBill.setStatus(
//                SplitBillStatus.CANCELLED);
//
//        splitBillRepository.save(
//                splitBill);
//
//        activityService.log(
//                splitBill,
//                ActivityType.CANCELLED,
//                "Split Bill Cancelled",
//                userId);
//
//        return mapper.toResponse(
//                splitBill);
//    }
//
//
//    @Transactional
//    public void markParticipantPaid(
//            Long participantId,
//            String transactionRefNo,
//            Long participantBankId ) {
//
//        SplitBillParticipant participant =
//                participantRepository
//                        .findById(participantId)
//                        .orElseThrow(() ->
//                                new ValidationException(
//                                        "Participant not found"));
//
//        participant.setPaymentStatus(
//                SplitPaymentStatus.PAID);
//
//        participant.setTransactionRefNo(
//                transactionRefNo);
//
//        participant.setUserBankId(participantBankId);
//
//        participant.setPaidAmount(
//                participant.getShareAmount());
//
//        participant.setRemainingAmount(
//                BigDecimal.ZERO);
//
//        participant.setPaidAt(
//                LocalDateTime.now());
//
//        participantRepository.save(
//                participant);
//
//        SplitBill splitBill =
//                splitBillRepository
//                        .findById(
//                                participant.getSplitBillId())
//                        .orElseThrow(() ->
//                                new ValidationException(
//                                        "Split Bill not found"));
//
//        splitBill.setPaidAmount(
//                splitBill.getPaidAmount()
//                        .add(
//                                participant.getShareAmount()));
//
//        splitBill.setRemainingAmount(
//                splitBill.getRemainingAmount()
//                        .subtract(
//                                participant.getShareAmount()));
//
//        splitBillRepository.save(
//                splitBill);
//
//        updateBillStatus(
//                splitBill.getSplitBillId());
//    }
//
//    private void updateBillStatus(
//            Long splitBillId) {
//
//        SplitBill splitBill =
//                splitBillRepository.findById(
//                                splitBillId)
//                        .orElseThrow(() ->
//                                new ValidationException(
//                                        "Split Bill not found"));
//
//        long totalParticipants =
//                participantRepository.countBySplitBillId(
//                        splitBillId);
//
//        long paidParticipants =
//                participantRepository
//                        .countBySplitBillIdAndPaymentStatus(
//                                splitBillId,
//                                SplitPaymentStatus.PAID);
//
//        if (paidParticipants == 0) {
//
//            splitBill.setStatus(
//                    SplitBillStatus.ACTIVE);
//
//        } else if (paidParticipants < totalParticipants) {
//
//            splitBill.setStatus(
//                    SplitBillStatus.PARTIALLY_PAID);
//            splitBill.setUpdatedAt(LocalDateTime.now());
//
//
//        } else {
//
//            splitBill.setStatus(
//                    SplitBillStatus.FULLY_PAID);
//            splitBill.setUpdatedAt(LocalDateTime.now());
//
//            activityService.log(
//                    splitBill,
//                    ActivityType.COMPLETED,
//                    "Split Bill Fully Settled",
//                    splitBill.getCreatedByUserId());
//        }
//
//        splitBillRepository.save(
//                splitBill);
//    }
//
//
//    @Transactional
//    public void payShare(
//            PaySplitBillRequest request) {
//
//        SplitBillParticipant participant =
//                participantRepository
//                        .findBySplitBillIdAndUserId(
//                                request.getSplitBillId(),
//                                request.getParticipantUserId())
//                        .orElseThrow(() ->
//                                new ValidationException(
//                                        "Participant not found"));
//
//        if(participant.getPaymentStatus()
//                == SplitPaymentStatus.PAID){
//
//            throw new ValidationException(
//                    "Share already paid");
//        }
//
//        SplitBill splitBill =
//                splitBillRepository
//                        .findById(
//                                request.getSplitBillId())
//                        .orElseThrow(() ->
//                                new ValidationException(
//                                        "Split Bill not found"));
//
//        TransferTransactionRequest transferRequest =
//                TransferTransactionRequest.builder()
//                        .senderUserId(
//                                participant.getUserId())
//
//                        .senderBankId(
//                                request.getParticipantBankId())
//
//                        .receiverUserId(
//                                splitBill.getCreatedByUserId())
//
//                        .receiverBankId(
//                                splitBill.getReceiverBankId())
//
//                        .amount(
//                                participant.getShareAmount())
//
//                        .upiPin(
//                                request.getUpiPin())
//
//                        .transactionType(
//                                "TRANSFER")
//
//                        .remarks(
//                                "Split Bill Settlement")
//                        .build();
//
//        TransferTransactionResponse response =
//                transferService.transfer(
//                        transferRequest);
//
//        markParticipantPaid(
//                participant.getParticipantId(),
//                response.getTransactionRefNo(),
//                request.getParticipantBankId());
//
//        activityService.log(
//                splitBill,
//                ActivityType.PAYMENT_RECEIVED,
//                "Participant paid share",
//                participant.getUserId());
//    }
//
//
//}

package com.example.Action_Service.service;

import com.example.Action_Service.dto.*;
import com.example.Action_Service.entity.SplitBill;
import com.example.Action_Service.entity.SplitBillParticipant;
import com.example.Action_Service.enums.ActivityType;
import com.example.Action_Service.enums.SplitBillStatus;
import com.example.Action_Service.enums.SplitPaymentStatus;
import com.example.Action_Service.exception.ValidationException;
import com.example.Action_Service.mapper.SplitBillMapper;
import com.example.Action_Service.mapper.SplitParticipantMapper;
import com.example.Action_Service.repository.SplitBillParticipantRepository;
import com.example.Action_Service.repository.SplitBillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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

        log.info(
                "Split bill creation initiated | CreatedBy={} | Participants={} | Amount={}",
                request.getCreatedByUserId(),
                request.getParticipants().size(),
                request.getTotalAmount());

        validateSplitAmount(request);

        log.info(
                "Split amount validation successful | CreatedBy={}",
                request.getCreatedByUserId());

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

        log.info(
                "Split bill created successfully | SplitBillId={}",
                savedBill.getSplitBillId());

        List<SplitBillParticipant> participants =
                new ArrayList<>();

        for (CreateSplitParticipantRequest p :
                request.getParticipants()) {

            SplitBillParticipant participant =
                    SplitBillParticipant.builder()
                            .splitBillId(
                                    savedBill.getSplitBillId())
                            .userId(
                                    p.getUserId())
                            .shareAmount(
                                    p.getShareAmount())
                            .paidAmount(
                                    BigDecimal.ZERO)
                            .remainingAmount(
                                    p.getShareAmount())
                            .paymentStatus(
                                    SplitPaymentStatus.PENDING)
                            .build();

            participants.add(participant);
        }

        participantRepository.saveAll(
                participants);

        log.info(
                "Participants saved successfully | SplitBillId={} | Count={}",
                savedBill.getSplitBillId(),
                participants.size());

        activityService.log(
                savedBill,
                ActivityType.CREATED,
                "Split Bill Created",
                request.getCreatedByUserId());

        log.info(
                "Split bill activity logged | SplitBillId={}",
                savedBill.getSplitBillId());

        log.info(
                "Split bill creation completed successfully | SplitBillId={}",
                savedBill.getSplitBillId());

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

            log.warn(
                    "Split amount validation failed | Expected={} | Actual={}",
                    request.getTotalAmount(),
                    total);

            throw new ValidationException(
                    "Split amount mismatch");
        }
    }


    @Transactional
    public SplitBillResponse cancel(
            Long splitBillId,
            Long userId) {

        log.info(
                "Split bill cancellation initiated | SplitBillId={} | UserId={}",
                splitBillId,
                userId);

        SplitBill splitBill =
                splitBillRepository
                        .findById(splitBillId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Split bill not found | SplitBillId={}",
                                    splitBillId);

                            return new ValidationException(
                                    "Split Bill not found");
                        });

        splitBill.setStatus(
                SplitBillStatus.CANCELLED);

        SplitBill updatedBill =
                splitBillRepository.save(
                        splitBill);

        log.info(
                "Split bill status updated to CANCELLED | SplitBillId={}",
                splitBillId);

        activityService.log(
                updatedBill,
                ActivityType.CANCELLED,
                "Split Bill Cancelled",
                userId);

        log.info(
                "Split bill cancellation activity logged | SplitBillId={}",
                splitBillId);

        log.info(
                "Split bill cancelled successfully | SplitBillId={}",
                splitBillId);

        return mapper.toResponse(
                updatedBill);
    }


    @Transactional
    public void markParticipantPaid(
            Long participantId,
            String transactionRefNo,
            Long participantBankId) {

        log.info(
                "Mark participant paid initiated | ParticipantId={} | TransactionRefNo={}",
                participantId,
                transactionRefNo);

        SplitBillParticipant participant =
                participantRepository
                        .findById(participantId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Participant not found | ParticipantId={}",
                                    participantId);

                            return new ValidationException(
                                    "Participant not found");
                        });

        participant.setPaymentStatus(
                SplitPaymentStatus.PAID);

        participant.setTransactionRefNo(
                transactionRefNo);

        participant.setUserBankId(
                participantBankId);

        participant.setPaidAmount(
                participant.getShareAmount());

        participant.setRemainingAmount(
                BigDecimal.ZERO);

        participant.setPaidAt(
                LocalDateTime.now());

        participantRepository.save(
                participant);

        log.info(
                "Participant marked as PAID | ParticipantId={} | SplitBillId={}",
                participant.getParticipantId(),
                participant.getSplitBillId());

        SplitBill splitBill =
                splitBillRepository
                        .findById(
                                participant.getSplitBillId())
                        .orElseThrow(() -> {

                            log.warn(
                                    "Split bill not found while updating payment | SplitBillId={}",
                                    participant.getSplitBillId());

                            return new ValidationException(
                                    "Split Bill not found");
                        });

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

        log.info(
                "Split bill payment summary updated | SplitBillId={} | PaidAmount={} | RemainingAmount={}",
                splitBill.getSplitBillId(),
                splitBill.getPaidAmount(),
                splitBill.getRemainingAmount());

        updateBillStatus(
                splitBill.getSplitBillId());

        log.info(
                "Participant payment processing completed | ParticipantId={} | SplitBillId={}",
                participantId,
                splitBill.getSplitBillId());
    }

    private void updateBillStatus(
            Long splitBillId) {

        log.info(
                "Updating split bill status | SplitBillId={}",
                splitBillId);

        SplitBill splitBill =
                splitBillRepository.findById(
                                splitBillId)
                        .orElseThrow(() -> {

                            log.warn(
                                    "Split bill not found while updating status | SplitBillId={}",
                                    splitBillId);

                            return new ValidationException(
                                    "Split Bill not found");
                        });

        long totalParticipants =
                participantRepository.countBySplitBillId(
                        splitBillId);

        long paidParticipants =
                participantRepository
                        .countBySplitBillIdAndPaymentStatus(
                                splitBillId,
                                SplitPaymentStatus.PAID);

        log.info(
                "Split bill payment summary | SplitBillId={} | PaidParticipants={} | TotalParticipants={}",
                splitBillId,
                paidParticipants,
                totalParticipants);

        if (paidParticipants == 0) {

            splitBill.setStatus(
                    SplitBillStatus.ACTIVE);

            log.info(
                    "Split bill status changed to ACTIVE | SplitBillId={}",
                    splitBillId);

        } else if (paidParticipants < totalParticipants) {

            splitBill.setStatus(
                    SplitBillStatus.PARTIALLY_PAID);

            splitBill.setUpdatedAt(
                    LocalDateTime.now());

            log.info(
                    "Split bill status changed to PARTIALLY_PAID | SplitBillId={}",
                    splitBillId);

        } else {

            splitBill.setStatus(
                    SplitBillStatus.FULLY_PAID);

            splitBill.setUpdatedAt(
                    LocalDateTime.now());

            log.info(
                    "Split bill status changed to FULLY_PAID | SplitBillId={}",
                    splitBillId);

            activityService.log(
                    splitBill,
                    ActivityType.COMPLETED,
                    "Split Bill Fully Settled",
                    splitBill.getCreatedByUserId());

            log.info(
                    "Split bill completion activity logged | SplitBillId={}",
                    splitBillId);
        }

        splitBillRepository.save(
                splitBill);

        log.info(
                "Split bill status updated successfully | SplitBillId={} | Status={}",
                splitBillId,
                splitBill.getStatus());
    }



    @Transactional
    public void payShare(
            PaySplitBillRequest request) {

        log.info(
                "Split bill payment initiated | SplitBillId={} | ParticipantUserId={}",
                request.getSplitBillId(),
                request.getParticipantUserId());

        SplitBillParticipant participant =
                participantRepository
                        .findBySplitBillIdAndUserId(
                                request.getSplitBillId(),
                                request.getParticipantUserId())
                        .orElseThrow(() -> {

                            log.warn(
                                    "Participant not found | SplitBillId={} | UserId={}",
                                    request.getSplitBillId(),
                                    request.getParticipantUserId());

                            return new ValidationException(
                                    "Participant not found");
                        });

        if (participant.getPaymentStatus()
                == SplitPaymentStatus.PAID) {

            log.warn(
                    "Participant already paid | ParticipantId={}",
                    participant.getParticipantId());

            throw new ValidationException(
                    "Share already paid");
        }

        SplitBill splitBill =
                splitBillRepository
                        .findById(
                                request.getSplitBillId())
                        .orElseThrow(() -> {

                            log.warn(
                                    "Split bill not found | SplitBillId={}",
                                    request.getSplitBillId());

                            return new ValidationException(
                                    "Split Bill not found");
                        });

        log.info(
                "Initiating transfer for split bill payment | SplitBillId={} | Amount={}",
                splitBill.getSplitBillId(),
                participant.getShareAmount());

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

        log.info(
                "Transfer completed successfully | TransactionRefNo={}",
                response.getTransactionRefNo());

        markParticipantPaid(
                participant.getParticipantId(),
                response.getTransactionRefNo(),
                request.getParticipantBankId());

        activityService.log(
                splitBill,
                ActivityType.PAYMENT_RECEIVED,
                "Participant paid share",
                participant.getUserId());

        log.info(
                "Participant payment activity logged | SplitBillId={} | ParticipantId={}",
                splitBill.getSplitBillId(),
                participant.getParticipantId());

        log.info(
                "Split bill payment completed successfully | SplitBillId={} | ParticipantId={}",
                splitBill.getSplitBillId(),
                participant.getParticipantId());
    }
}
