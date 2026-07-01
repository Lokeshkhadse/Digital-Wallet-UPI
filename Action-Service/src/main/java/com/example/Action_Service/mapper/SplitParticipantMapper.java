package com.example.Action_Service.mapper;

import com.example.Action_Service.dto.SplitBillParticipantResponse;
import com.example.Action_Service.entity.SplitBillParticipant;
import org.springframework.stereotype.Component;

@Component
public class SplitParticipantMapper {

    public SplitBillParticipantResponse toResponse(
            SplitBillParticipant participant) {

        return SplitBillParticipantResponse.builder()
                .participantId(
                        participant.getParticipantId())
                .userId(
                        participant.getUserId())
                .userBankId(
                        participant.getUserBankId())
                .shareAmount(
                        participant.getShareAmount())
                .paidAmount(
                        participant.getPaidAmount())
                .remainingAmount(
                        participant.getRemainingAmount())
                .paymentStatus(
                        participant.getPaymentStatus())
                .transactionRefNo(
                        participant.getTransactionRefNo())
                .paidAt(
                        participant.getPaidAt())
                .build();
    }
}